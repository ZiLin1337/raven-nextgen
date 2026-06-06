package keystrokesmod.clickgui.components.impl;

import com.mojang.authlib.GameProfile;
import keystrokesmod.clickgui.animation.ScrollOffsetAnimation;
import keystrokesmod.utility.Theme;
import keystrokesmod.module.setting.impl.PlayerListSetting;
import keystrokesmod.utility.PlayerRelationsManager;
import keystrokesmod.utility.PlayerSkinCache;
import keystrokesmod.utility.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.GlStateManager;
import net.minecraft.util.Identifier;

import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListComponent extends AbstractTextInputComponent {
    private static final String CLOSE_ICON_PATH = "/assets/keystrokesmod/textures/gui/close.png";
    private static final float SELECTED_LIST_GAP = 4f;
    private static final int MAX_VISIBLE_SELECTED = 7;
    private static final int CLOSE_SIZE = 6;
    private static final float CLOSE_PAD = 3f;
    private static final float HEAD_SIZE = 8f;
    private static final float LIST_ROW_TEXT_SCALE = 0.56f;
    private static final float LIST_ROW_TEXT_Y_OFFSET = LIST_ROW_TEXT_SCALE;
    private static final float LIST_ROW_VISUAL_HEIGHT = ROW_HEIGHT - 1f;
    private final PlayerListSetting setting;
    private final ScrollOffsetAnimation selectedScrollAnim = new ScrollOffsetAnimation(200);

    private float lastMouseX;
    private float lastMouseY;

    public PlayerListComponent(PlayerListSetting setting, ModuleComponent moduleComponent, float o) {
        super(moduleComponent, o, setting.getPlaceholder(), setting.getMaxLength());
        this.setting = setting;
    }

    @Override public void render() { Layout layout = layout(false); renderLabel(layout, setting.getName()); renderTextField(layout); renderSelectedEntries(layout); }
    @Override public void drawScreen(int mouseX, int mouseY) { super.drawScreen(mouseX, mouseY); lastMouseX = mouseX; lastMouseY = mouseY; clampSelectedScroll(); }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!moduleComponent.isOpened || !moduleComponent.isVisible(this)) return false;
        Layout layout = layout(true);
        if (button == 0 && isTextFieldClicked(mouseX, mouseY, layout)) { setTextFieldFocused(true); return true; }
        if (button == 0 && handleSelectedEntryClick(mouseX, mouseY, layout)) return true;
        if (isTextFieldFocused()) { getTextField().setText(""); setTextFieldFocused(false); }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!moduleComponent.isOpened || !isTextFieldFocused()) return;
        if (keyCode == Keyboard.KEY_ESCAPE) { getTextField().setText(""); setTextFieldFocused(false); return; }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) { submitText(); setTextFieldFocused(false); return; }
        getTextField().textboxKeyTyped(typedChar, keyCode);
    }

    @Override public void onScroll(int scroll) {
        if (!moduleComponent.isOpened || !moduleComponent.isVisible(this)) return;
        if (!capturesCategoryScroll(lastMouseX, lastMouseY)) return;
        float delta = (float) keystrokesmod.module.impl.client.Gui.scrollSpeed.getInput() * (scroll / 120f);
        if (delta != 0f) selectedScrollAnim.extend(-delta);
        clampSelectedScroll();
    }

    @Override public void onGuiClosed() { super.onGuiClosed(); getTextField().setText(""); selectedScrollAnim.reset(0f); }
    @Override public float getHeightF() { int c = setting.getEntries().size(); float h = c == 0 ? 0f : SELECTED_LIST_GAP + Math.min(MAX_VISIBLE_SELECTED, c) * ROW_HEIGHT; return (2f * ROW_HEIGHT) + h; }
    @Override public boolean isBaseVisible() { return setting.visible; }
    @Override public String getGroupName() { return setting.group != null ? setting.group.getName() : ""; }
    public boolean capturesCategoryScroll(float x, float y) { return setting.getEntries().size() > MAX_VISIBLE_SELECTED && isMouseOverSelectedList(x, y); }
    public boolean containsClick(int x, int y) { Layout l = layout(true); return isTextFieldClicked(x, y, l) || isMouseOverSelectedList(x, y); }
    public void onExternalDataChanged() { clampSelectedScroll(); }

    private void submitText() {
        String name = getTextField().getText();
        if (name == null || name.trim().isEmpty()) return;
        if (setting.addPlayer(name)) getTextField().setText("");
        moduleComponent.updateSettingPositions();
        clampSelectedScroll();
    }

    private void renderSelectedEntries(Layout layout) {
        List<PlayerRelationsManager.PlayerEntry> entries = setting.getEntries();
        if (entries.isEmpty()) return;
        float selectedTop = getSelectedTop(layout);
        float selectedHeight = getSelectedVisibleHeight(entries.size());
        float scrollOffset = moduleComponent.categoryComponent.getModuleY() - layout.cy;
        RenderUtils.scissorPushGui(layout.left, selectedTop + scrollOffset, layout.right - layout.left, selectedHeight);
        float offsetPx = selectedScrollAnim.getValue();
        int firstRow = (int) (offsetPx / ROW_HEIGHT);
        int end = Math.min(firstRow + MAX_VISIBLE_SELECTED + 1, entries.size());
        Map<String, PlayerListEntry> playerInfoMap = getPlayerInfoMap();
        for (int i = firstRow; i < end; i++) {
            PlayerRelationsManager.PlayerEntry entry = entries.get(i);
            float rowTop = selectedTop - offsetPx + i * ROW_HEIGHT;
            RenderUtils.drawRect(layout.left, rowTop, layout.right, rowTop + ROW_HEIGHT - 1f, (i % 2 == 0) ? 0xFF1A1A2A : 0xFF1E1E2E);
            renderPlayerHead(entry, playerInfoMap.get(entry.getKey()), layout.left + 2f, rowTop + (LIST_ROW_VISUAL_HEIGHT - HEAD_SIZE) / 2f);
            drawScaledText(entry.getDisplayName(), layout.left + 13f, centeredScaledTextY(rowTop, LIST_ROW_VISUAL_HEIGHT, LIST_ROW_TEXT_SCALE) + LIST_ROW_TEXT_Y_OFFSET, 0xFFCCCCCC, LIST_ROW_TEXT_SCALE);
            renderCloseIcon(layout.right, rowTop);
        }
        RenderUtils.scissorPop();
    }

    private boolean handleSelectedEntryClick(int mouseX, int mouseY, Layout layout) {
        List<PlayerRelationsManager.PlayerEntry> entries = setting.getEntries();
        float offsetPx = selectedScrollAnim.getValue();
        for (int i = 0; i < entries.size(); i++) {
            float rowTop = getSelectedTop(layout) - offsetPx + i * ROW_HEIGHT;
            if (isOverClose(mouseX, mouseY, rowTop, layout.right)) { setting.removePlayer(entries.get(i).getKey()); moduleComponent.updateSettingPositions(); clampSelectedScroll(); return true; }
        }
        return false;
    }

    private void renderPlayerHead(PlayerRelationsManager.PlayerEntry entry, PlayerListEntry playerInfo, float x, float y) {
        Identifier skin = PlayerSkinCache.getSkin(entry.getDisplayName(), playerInfo);
        if (skin == null) return;
        boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        try {
            RenderUtils.prepareGuiTextureRenderState();
            MinecraftClient.getInstance().getTextureManager().bindTexture(skin);
            GlStateManager.color(1f, 1f, 1f, 1f);
            net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8f, 8f, 8, 8, (int) HEAD_SIZE, (int) HEAD_SIZE, 64f, 64f);
            net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 40f, 8f, 8, 8, (int) HEAD_SIZE, (int) HEAD_SIZE, 64f, 64f);
        } finally { RenderUtils.restoreGuiRenderState(depth, blend, depthMask); }
    }

    private Map<String, PlayerListEntry> getPlayerInfoMap() {
        Map<String, PlayerListEntry> map = new HashMap<>();
        if (MinecraftClient.getInstance().getNetHandler() == null) return map;
        for (PlayerListEntry info : MinecraftClient.getInstance().getNetHandler().getPlayerInfoMap()) {
            if (info == null) continue;
            GameProfile profile = info.getGameProfile();
            if (profile == null || profile.getName() == null) continue;
            map.put(profile.getName().toLowerCase(), info);
        }
        return map;
    }

    private void clampSelectedScroll() { int c = setting.getEntries().size(); float max = Math.max(0f, (c - MAX_VISIBLE_SELECTED) * ROW_HEIGHT); selectedScrollAnim.clampTarget(0f, max); if (selectedScrollAnim.getValue() > max) selectedScrollAnim.reset(max); }
    private boolean isMouseOverSelectedList(float mx, float my) { List<PlayerRelationsManager.PlayerEntry> e = setting.getEntries(); if (e.isEmpty()) return false; Layout l = layout(true); float top = getSelectedTop(l); float h = getSelectedVisibleHeight(e.size()); return mx >= l.left && mx <= l.right && my >= top && my < top + h; }
    private float getSelectedTop(Layout l) { return l.contentTop + SELECTED_LIST_GAP; }
    private float getSelectedVisibleHeight(int c) { return Math.min(MAX_VISIBLE_SELECTED, c) * ROW_HEIGHT; }
    private void renderCloseIcon(float right, float rowTop) { Identifier close = RenderUtils.getIcon(CLOSE_ICON_PATH); if (close == null) return; float cx = right - CLOSE_SIZE - CLOSE_PAD; float cy = rowTop + (LIST_ROW_VISUAL_HEIGHT - CLOSE_SIZE) / 2f; RenderUtils.drawIcon(close, cx, cy, CLOSE_SIZE, Theme.getGradient(Theme.hiddenBind[0], Theme.hiddenBind[1], 0)); }
    private boolean isOverClose(float mx, float my, float rowTop, float right) { float cx = right - CLOSE_SIZE - CLOSE_PAD; float cy = rowTop + (LIST_ROW_VISUAL_HEIGHT - CLOSE_SIZE) / 2f; return mx >= cx && mx <= cx + CLOSE_SIZE && my >= cy && my <= cy + CLOSE_SIZE; }
}