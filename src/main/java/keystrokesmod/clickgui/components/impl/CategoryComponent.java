package keystrokesmod.clickgui.components.impl;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.animation.ScrollOffsetAnimation;
import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.profile.Manager;
import keystrokesmod.utility.profile.Profile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CategoryComponent {
    private static long interactionSequence;
    private static final Map<Module.category, CategoryIconStacks> CATEGORY_ICON_STACKS = buildCategoryIconStacks();

    public List<ModuleComponent> modules = new CopyOnWriteArrayList<>();
    public Module.category category;
    public boolean opened;
    public float width;
    public float y;
    public float x;
    public float titleHeight;
    public boolean dragging;
    public float xx;
    public float yy;
    public boolean hovering = false;
    public boolean hoveringOverCategory = false;
    public Timer smoothTimer;
    private Timer textTimer;
    public float big;

    private static final int TRANSLUCENT_BACKGROUND = new Color(0, 0, 0, 110).getRGB();
    private static final int REGULAR_OUTLINE = new Color(81, 99, 149).getRGB();
    private static final int REGULAR_OUTLINE2 = new Color(97, 67, 133).getRGB();
    private static final int CATEGORY_NAME_COLOR = new Color(220, 220, 220).getRGB();

    private float lastHeight;
    private float lastNamePos;
    private float animationStartNamePos;
    public float moduleY;
    private float screenHeight;
    private float screenWidth;
    private float animationStartHeight;

    private final ScrollOffsetAnimation scrollAnim = new ScrollOffsetAnimation(200);

    public long lastInteractedTime = 0L;

    private static final class CategoryLayoutMetrics {
        private final float visibleHeight;
        private final float minScrollY;
        private final float contentBottom;

        private CategoryLayoutMetrics(float visibleHeight, float minScrollY, float contentBottom) {
            this.visibleHeight = visibleHeight;
            this.minScrollY = minScrollY;
            this.contentBottom = contentBottom;
        }
    }

    private static final class CategoryIconStacks {
        private final ItemStack normalStack;
        private final ItemStack activeStack;

        private CategoryIconStacks(ItemStack normalStack, ItemStack activeStack) {
            this.normalStack = normalStack;
            this.activeStack = activeStack;
        }
    }

    public CategoryComponent(Module.category category) {
        this.category = category;
        this.width = 92;
        this.x = 5;
        this.moduleY = this.y = 5;
        this.titleHeight = 13;
        float moduleRenderY = this.titleHeight + 3;
        scrollAnim.reset(this.moduleY);

        this.lastHeight = this.y + this.titleHeight + 4;
        this.animationStartHeight = this.lastHeight;

        for (Module mod : Raven.getModuleManager().inCategory(this.category)) {
            ModuleComponent b = new ModuleComponent(mod, this, moduleRenderY);
            this.modules.add(b);
            moduleRenderY += 16;
        }
    }

    public List<ModuleComponent> getModules() { return this.modules; }

    public void reloadModules() {
        Map<String, Boolean> openStates = captureModuleOpenStates();
        this.modules.clear();
        this.titleHeight = 13;
        float moduleRenderY = this.titleHeight + 3;
        for (Module mod : Raven.getModuleManager().inCategory(this.category)) {
            ModuleComponent component = new ModuleComponent(mod, this, moduleRenderY);
            component.restoreOpenState(Boolean.TRUE.equals(openStates.get(mod.getName())));
            this.modules.add(component);
            moduleRenderY += 16;
        }
        syncAfterModuleReload();
    }

    public void reloadModules(boolean isProfile) {
        Map<String, Boolean> openStates = captureModuleOpenStates();
        this.modules.clear();
        this.titleHeight = 13;
        float moduleRenderY = this.titleHeight + 3;
        if ((this.category == Module.category.profiles && isProfile) || (this.category == Module.category.scripts && !isProfile)) {
            ModuleComponent manager = new ModuleComponent(isProfile ? new Manager() : new keystrokesmod.script.Manager(), this, moduleRenderY);
            manager.restoreOpenState(Boolean.TRUE.equals(openStates.get(manager.mod.getName())));
            this.modules.add(manager);
            if ((Raven.profileManager == null && isProfile) || (Raven.scriptManager == null && !isProfile)) return;
            if (isProfile) {
                for (Profile profile : Raven.profileManager.profiles) {
                    moduleRenderY += 16;
                    ModuleComponent b = new ModuleComponent(profile.getModule(), this, moduleRenderY);
                    b.restoreOpenState(Boolean.TRUE.equals(openStates.get(profile.getModule().getName())));
                    this.modules.add(b);
                }
            } else {
                Collection<Module> mods = Raven.scriptManager.scripts.values();
                List<Module> sorted = mods.stream().sorted(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
                for (Module m : sorted) {
                    moduleRenderY += 16;
                    ModuleComponent b = new ModuleComponent(m, this, moduleRenderY);
                    b.restoreOpenState(Boolean.TRUE.equals(openStates.get(m.getName())));
                    this.modules.add(b);
                }
            }
        }
        syncAfterModuleReload();
    }

    private Map<String, Boolean> captureModuleOpenStates() {
        Map<String, Boolean> map = new HashMap<>();
        for (ModuleComponent mc : this.modules) { if (mc.mod != null) map.put(mc.mod.getName(), mc.isOpened); }
        return map;
    }

    private void syncAfterModuleReload() {
        CategoryLayoutMetrics lm = computeLayoutMetrics(this.opened || this.smoothTimer != null);
        float clamped = Math.max(lm.minScrollY, Math.min(this.y, scrollAnim.getTarget()));
        this.moduleY = clamped;
        scrollAnim.reset(clamped);
        if (this.opened && !this.modules.isEmpty()) { this.big = lm.visibleHeight; this.lastHeight = lm.contentBottom; return; }
        if (!this.opened && this.smoothTimer == null) this.big = 0f;
        this.lastHeight = this.y + this.titleHeight + 4;
    }

    public void setX(float newX, boolean limit) {
        if (limit) { newX = Math.max(newX, 2); newX = Math.min(newX, screenWidth - this.width - 4); }
        this.x = newX;
    }

    public void setY(float y, boolean limit) {
        if (limit) { y = Math.max(y, 1); y = Math.min(y, screenHeight - this.titleHeight - 5); }
        float scrollOff = scrollAnim.getTarget() - this.y;
        this.y = y;
        float newTarget = y + scrollOff;
        this.moduleY = newTarget;
        scrollAnim.reset(newTarget);
    }

    public void overTitle(boolean d) { this.dragging = d; }
    public boolean isOpened() { return this.opened; }
    public void markInteracted() { this.lastInteractedTime = ++interactionSequence; }

    public void mouseClicked(boolean on) {
        this.animationStartHeight = getCurrentAnimatedCategoryHeight();
        this.animationStartNamePos = getCurrentAnimatedNamePos();
        this.opened = on;
        (this.smoothTimer = new Timer(250f)).start();
        (this.textTimer = new Timer(250f)).start();
    }

    public void onScroll(int mouseScrollInput) { onScroll(mouseScrollInput, Float.NaN, Float.NaN); }

    public void onScroll(int mouseScrollInput, float mouseX, float mouseY) {
        for (ModuleComponent mod : this.modules) mod.onScroll(mouseScrollInput);
        if (!hoveringOverCategory || !this.opened) return;
        if (!Float.isNaN(mouseX) && !Float.isNaN(mouseY)) {
            for (ModuleComponent mod : this.modules) {
                for (Component comp : mod.settings) {
                    if (!mod.isOpened || !mod.isVisible(comp)) continue;
                    if (comp instanceof AbstractSearchListComponent && ((AbstractSearchListComponent) comp).capturesCategoryScroll(mouseX, mouseY)) return;
                    if (comp instanceof PlayerListComponent && ((PlayerListComponent) comp).capturesCategoryScroll(mouseX, mouseY)) return;
                    if (comp instanceof StringListComponent && ((StringListComponent) comp).capturesCategoryScroll(mouseX, mouseY)) return;
                }
            }
        }
        this.markInteracted();
        float scrollSpeed = (float) Gui.scrollSpeed.getInput();
        float minScrollY = computeMinScrollY();
        float maxScrollY = this.y;
        float delta = scrollSpeed * (mouseScrollInput / 120f);
        if (delta != 0f) scrollAnim.extend(delta);
        scrollAnim.clampTarget(minScrollY, maxScrollY);
    }

    private float getTotalScrollExtentHeightF() {
        float total = 0f;
        for (ModuleComponent c : this.modules) total += c.getScrollExtentHeightF();
        return total;
    }

    private float computeMinScrollY() { return computeLayoutMetrics(false).minScrollY; }

    public void render() {
        this.width = 92;
        RavenFontRenderer titleRenderer = Gui.getClickGuiHeaderFontRenderer();
        if (smoothTimer != null && System.currentTimeMillis() - smoothTimer.last >= 280) smoothTimer = null;
        if (textTimer != null && System.currentTimeMillis() - textTimer.last >= 280) textTimer = null;
        for (ModuleComponent c : this.modules) c.updateAnimationState();
        CategoryLayoutMetrics lm = computeLayoutMetrics(this.opened || smoothTimer != null);
        big = (!this.opened && smoothTimer == null) ? 0f : lm.visibleHeight;
        float maxScrollY = this.y;
        float minScrollY = lm.minScrollY;
        scrollAnim.clampTarget(minScrollY, maxScrollY);
        moduleY = scrollAnim.getValue();
        moduleY = Math.max(minScrollY, Math.min(maxScrollY, moduleY));
        float middlePos = this.x + this.width / 2 - titleRenderer.getStringWidth(this.category.name()) / 2.0f;
        float contentBottom = lm.contentBottom;
        float extra;
        if (smoothTimer != null) {
            float targetHeight = this.opened ? contentBottom : (this.y + this.titleHeight + 4);
            extra = smoothTimer.getValueFloat(animationStartHeight, targetHeight, 1);
            if ((this.opened && extra > targetHeight) || (!this.opened && extra < targetHeight)) extra = targetHeight;
        } else {
            extra = contentBottom;
        }
        float targetNamePos = this.opened ? middlePos : (this.x + 12);
        float namePos = (textTimer == null) ? targetNamePos : textTimer.getValueFloat(animationStartNamePos, targetNamePos, 1);
        this.lastNamePos = namePos;
        this.lastHeight = extra;
        GL11.glPushMatrix();
        RenderUtils.drawRoundedGradientOutlinedRectangle(this.x - 2, this.y, this.x + this.width + 2, extra, 10, TRANSLUCENT_BACKGROUND,
                ((opened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(Utils.getChroma(2, 0), 0.5) : REGULAR_OUTLINE,
                ((opened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(Utils.getChroma(2, 700), 0.5) : REGULAR_OUTLINE2);
        renderItemForCategory(this.category, (int) (this.x + 1), (int) (this.y + 4), opened || hovering);
        titleRenderer.drawString(this.category.name(), namePos, this.y + 4, CATEGORY_NAME_COLOR, false);
        float moduleAreaTop = this.y + this.titleHeight + 3;
        float scissorBottom = extra - 2f;
        float moduleAreaHeight = Math.max(0f, scissorBottom - moduleAreaTop);
        if (this.opened || smoothTimer != null) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtils.scissor(0, moduleAreaTop, this.x + this.width + 4, moduleAreaHeight);
            float scrollOffset = moduleY - this.y;
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, scrollOffset, 0f);
            for (Component c2 : this.modules) c2.render();
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        GL11.glPopMatrix();
    }

    public void updateHeight() {
        float y = this.titleHeight + 3;
        for (Component component : this.modules) { component.updateHeight(y); y += component.getHeightF(); }
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getModuleY() { return this.moduleY; }
    public float getWidth() { return this.width; }

    public void mousePosition(int mouseX, int mouseY, boolean isTopmostUnderCursor) {
        if (this.dragging) {
            float newX = mouseX - this.xx;
            float newY = mouseY - this.yy;
            newX = Math.max(newX, 2); newX = Math.min(newX, screenWidth - this.width - 4);
            newY = Math.max(newY, 1); newY = Math.min(newY, (int)(screenHeight - this.titleHeight - 5));
            this.setX(newX, false); this.setY(newY, false);
        }
        hoveringOverCategory = isTopmostUnderCursor && overCategory(mouseX, mouseY);
        hovering = isTopmostUnderCursor && overTitle(mouseX, mouseY);
    }

    public boolean overTitle(int x, int y) { return x >= this.x && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.titleHeight + 1; }
    public boolean overCategory(int x, int y) { return x >= this.x - 2 && x <= this.x + this.width + 2 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.titleHeight + big + 1; }
    public boolean draggable(int x, int y) { return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.titleHeight; }
    public boolean overRect(int x, int y) { return x >= this.x - 2 && x <= this.x + this.width + 2 && y >= this.y && y <= lastHeight; }

    private void renderItemForCategory(Module.category category, int x, int y, boolean enchant) {
        ItemRenderer renderItem = MinecraftClient.getInstance().getItemRenderer();
        double scale = 0.55;
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);
        CategoryIconStacks icons = CATEGORY_ICON_STACKS.get(category);
        ItemStack stack = icons == null ? null : (enchant ? icons.activeStack : icons.normalStack);
        if (stack != null) {
            DiffuseLighting.enableGuiLighting();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glTranslated(x / scale, y / scale, 0);
            renderItem.renderItem(null, stack, 0, 0, 0);
            GL11.glEnable(GL11.GL_BLEND);
            DiffuseLighting.disableGuiLighting();
        }
        GL11.glPopMatrix();
    }

    private float getCurrentCategoryBottomFromContent() {
        if (!this.modules.isEmpty() && (this.opened || smoothTimer != null))
            return Math.min(this.y + this.titleHeight + big + 4, this.y + (this.screenHeight * 0.9f));
        return this.y + this.titleHeight + 4;
    }

    private float getCurrentAnimatedNamePos() {
        if (textTimer != null) return lastNamePos;
        float mp = this.x + this.width / 2 - Gui.getClickGuiHeaderFontRenderer().getStringWidth(this.category.name()) / 2.0f;
        return this.opened ? mp : (this.x + 12);
    }

    private float getCurrentAnimatedCategoryHeight() {
        if (this.lastHeight > 0) return this.lastHeight;
        if (!this.modules.isEmpty() && (this.opened || this.smoothTimer != null)) {
            float mh = 0f;
            for (ModuleComponent c : this.modules) mh += c.getHeightF();
            return this.y + this.titleHeight + mh + 4;
        }
        return this.y + this.titleHeight + 4;
    }

    public void setScreenSize(float screenWidth, float screenHeight) { this.screenWidth = screenWidth; this.screenHeight = screenHeight; }
    public void limitPositions() { setX(this.x, true); setY(this.y, true); }

    public void applySavedState(float x, float y, boolean opened, boolean clampToScreen) {
        if (clampToScreen) { setX(x, true); setY(y, true); }
        else {
            float scrollOff = scrollAnim.getTarget() - this.y;
            this.x = x; this.y = y;
            this.moduleY = y + scrollOff;
            scrollAnim.reset(this.moduleY);
        }
        this.opened = opened;
        smoothTimer = null; textTimer = null;
        if (opened && !this.modules.isEmpty()) {
            CategoryLayoutMetrics lm = computeLayoutMetrics(true);
            this.big = lm.visibleHeight; this.lastHeight = lm.contentBottom;
        } else { this.big = 0f; this.lastHeight = this.y + this.titleHeight + 4; }
        this.moduleY = this.y; scrollAnim.reset(this.y);
    }

    public void onGuiClosed() {
        if (smoothTimer != null || textTimer != null) {
            float fh = this.y + this.titleHeight;
            if (this.opened && !this.modules.isEmpty()) {
                float mh = 0f;
                for (ModuleComponent c : this.modules) mh += c.getHeightF();
                fh += mh + 4;
            } else fh += 4;
            this.lastHeight = fh;
        }
        smoothTimer = null; textTimer = null;
        moduleY = scrollAnim.getTarget();
        scrollAnim.reset(moduleY);
    }

    private CategoryLayoutMetrics computeLayoutMetrics(boolean updateModuleOffsets) {
        if (this.modules.isEmpty() || (!this.opened && this.smoothTimer == null))
            return new CategoryLayoutMetrics(0f, this.y, this.y + this.titleHeight + 4);
        float maxH = (this.screenHeight * 0.9f) - this.titleHeight - 4;
        float visibleH = 0f, totalScroll = 0f, moduleOff = this.titleHeight + 3;
        for (ModuleComponent c : this.modules) {
            if (updateModuleOffsets) c.updateHeight(moduleOff);
            float ch = c.getHeightF();
            moduleOff += ch;
            totalScroll += c.getScrollExtentHeightF();
            if (visibleH < maxH) visibleH += Math.min(ch, maxH - visibleH);
        }
        float viewport = Math.min(maxH, totalScroll);
        float overflow = Math.max(0f, totalScroll - viewport);
        float minScrollY = overflow > 0f ? this.y - overflow : this.y;
        float maxBottom = this.y + (this.screenHeight * 0.9f);
        float contentBottom = Math.min(this.y + this.titleHeight + visibleH + 4, maxBottom);
        return new CategoryLayoutMetrics(Math.max(0f, visibleH), minScrollY, contentBottom);
    }

    private static Map<Module.category, CategoryIconStacks> buildCategoryIconStacks() {
        EnumMap<Module.category, CategoryIconStacks> map = new EnumMap<>(Module.category.class);
        for (Module.category cat : Module.category.values()) {
            ItemStack normal = createCategoryIconStack(cat, false);
            ItemStack active = createCategoryIconStack(cat, true);
            if (normal != null && active != null) map.put(cat, new CategoryIconStacks(normal, active));
        }
        return map;
    }

    private static ItemStack createCategoryIconStack(Module.category category, boolean active) {
        ItemStack stack;
        switch (category) {
            case combat: stack = new ItemStack(Items.DIAMOND_SWORD); break;
            case movement: stack = new ItemStack(Items.DIAMOND_BOOTS); break;
            case player: stack = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE); break;
            case world: stack = new ItemStack(Items.FILLED_MAP); break;
            case render: stack = new ItemStack(Items.ENDER_EYE); break;
            case minigames: stack = new ItemStack(Items.GOLD_INGOT); break;
            case fun: stack = new ItemStack(Items.SLIME_BALL); break;
            case other: stack = new ItemStack(Items.CLOCK); break;
            case client: stack = new ItemStack(Items.COMPASS); break;
            case profiles: stack = new ItemStack(Items.BOOK); break;
            case scripts: stack = new ItemStack(Items.REDSTONE); break;
            default: return null;
        }
        if (active) {
            stack.addEnchantment(Registries.ENCHANTMENT.getEntry(Enchantments.UNBREAKING), 2);
        }
        return stack;
    }


    // Added for ClickGui compatibility
    public boolean isMouseOver(int x, int y) {
        return overTitle(x, y) || overCategory(x, y);
    }
    public void onClick(int mx, int my, int button) {
        mouseClicked(!this.opened);
        markInteracted();
    }
    public void onMouseRelease() {
        overTitle(false);
    }
    public void onMouseMove(int mx, int my) {
        mousePosition(mx, my, true);
    }
}
