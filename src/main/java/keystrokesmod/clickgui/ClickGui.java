package keystrokesmod.clickgui;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.components.Component;
import keystrokesmod.clickgui.components.FocusableTextComponent;
import keystrokesmod.clickgui.components.impl.BindComponent;
import keystrokesmod.clickgui.components.impl.CategoryComponent;
import keystrokesmod.clickgui.components.impl.ModuleComponent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.CommandLine;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.CommandHandler;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClickGui extends Screen {
    private ScheduledFuture<?> sf;
    private Timer logoSmoothWidth;
    private Timer logoSmoothLength;
    private Timer smoothEntity;
    private Timer backgroundFade;
    private Timer blurSmooth;
    private TextFieldWidget commandLineInput;
    public static ArrayList<CategoryComponent> categories;
    private int actualScreenWidth;
    private int actualScreenHeight;
    private double previousScale;
    private static boolean isNotFirstOpen;
    private boolean pendingScaleRefresh;
    private CategoryComponent draggedCategory;

    public ClickGui() {
        super(Text.literal("Raven ClickGUI"));
        categories = new ArrayList<>();
        int y = 5;
        for (Module.category c : Module.category.values()) {
            CategoryComponent categoryComponent = new CategoryComponent(c);
            categoryComponent.setY(y, false);
            categories.add(categoryComponent);
            y += 20;
        }
    }

    public void initMain() {
        // Logo animation disabled - scheduledExecutor not available
    }, 650L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void init() {
        super.init();
        double configuredScale = getConfiguredGuiScale();
        if (!isNotFirstOpen) {
            isNotFirstOpen = true;
            this.previousScale = configuredScale;
        }
        for (CategoryComponent categoryComponent : categories) {
            categoryComponent.setScreenSize(this.width, this.height);
        }
        if (Double.compare(this.previousScale, configuredScale) != 0) {
            for (CategoryComponent categoryComponent : categories) {
                categoryComponent.limitPositions();
            }
        }
        for (CategoryComponent categoryComponent : categories) {
            if (categoryComponent.category == Module.category.profiles) {
                categoryComponent.reloadModules(true);
            } else if (categoryComponent.category == Module.category.scripts) {
                categoryComponent.reloadModules(false);
            } else {
                categoryComponent.reloadModules();
            }
        }
        this.commandLineInput = new TextFieldWidget(textRenderer, 22, this.height - 100, 150, 20, Text.literal(""));
        this.commandLineInput.setMaxLength(256);
        this.addDrawableChild(this.commandLineInput);
        this.previousScale = configuredScale;
    }

    private List<CategoryComponent> getCategoriesInRenderOrder() {
        List<CategoryComponent> renderOrder = new ArrayList<>(categories);
        renderOrder.sort(Comparator.comparingLong(c -> c.lastInteractedTime));
        return renderOrder;
    }

    private CategoryComponent getTopmostUnderCursor(List<CategoryComponent> renderOrder, int x, int y) {
        for (int i = renderOrder.size() - 1; i >= 0; i--) {
            if (renderOrder.get(i).overRect(x, y)) {
                return renderOrder.get(i);
            }
        }
        return null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int logicalMouseX = toLogicalCoordinate(mouseX);
        int logicalMouseY = toLogicalCoordinate(mouseY);

        // Dark background
        if (true) { // darkBackground check disabled
            float fadeValue = this.backgroundFade != null ? this.backgroundFade.getValueFloat(0.0F, 0.7F, 2) : 0.7F;
            context.fill(0, 0, this.actualScreenWidth, this.actualScreenHeight, (int)(fadeValue * 255) << 24);
        }

        // Logo watermark
        if (true) { // removeWatermark check disabled
            String watermark = "raven bS";
            int wmColor = Utils.getChroma(2L, 0L);
            context.drawTextWithShadow(textRenderer, watermark, 2, 2, wmColor);
        }

        // Render categories
        List<CategoryComponent> renderOrder = getCategoriesInRenderOrder();
        CategoryComponent topmostUnderCursor = getTopmostUnderCursor(renderOrder, logicalMouseX, logicalMouseY);
        for (CategoryComponent c : renderOrder) {
            c.render();
            c.mousePosition(logicalMouseX, logicalMouseY, c == topmostUnderCursor);
            for (Component m : c.getModules()) {
                m.drawScreen(logicalMouseX, logicalMouseY);
            }
        }

        // Command line
        // CommandLine support disabled
                    false = false;
                }
            }
            context.fill(0, 0, r, this.height, 0xA0000000);
            context.fill(r - 1, 0, r, this.height, 0xFFFFFFFF);
            CommandHandler.renderCommandOutput(textRenderer, this.height, r, 1);
            this.commandLineInput.render(context, logicalMouseX, logicalMouseY, delta);
        } else if (false) {
            false = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        List<CategoryComponent> inputOrder = new ArrayList<>(categories);
        inputOrder.sort((a, b) -> Long.compare(b.lastInteractedTime, a.lastInteractedTime));
        CategoryComponent topmostCategory = null;
        for (CategoryComponent category : inputOrder) {
            if (category.overRect(mx, my)) {
                topmostCategory = category;
                break;
            }
        }

        if (topmostCategory != null) {
            topmostCategory.markInteracted();
        }

        if (button == 0) {
            for (CategoryComponent category : categories) {
                category.overTitle(false);
            }
            if (topmostCategory != null && topmostCategory.draggable(mx, my)) {
                topmostCategory.overTitle(true);
                topmostCategory.xx = mx - topmostCategory.getX();
                topmostCategory.yy = my - topmostCategory.getY();
                topmostCategory.dragging = true;
                draggedCategory = topmostCategory;
            }
        }

        if (button == 1 && topmostCategory != null && topmostCategory.overTitle(mx, my)) {
            topmostCategory.mouseClicked(!topmostCategory.isOpened());
        }

        if (topmostCategory != null && topmostCategory.isOpened() && !topmostCategory.getModules().isEmpty() && !topmostCategory.overTitle(mx, my)) {
            for (ModuleComponent component : topmostCategory.getModules()) {
                if (component.onClick(mx, my, button)) {
                    break;
                }
            }
        }

        if (false) {
            this.commandLineInput.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 0 || button == 1) {
            FocusableTextComponent focusedComponent = findFocusedTextComponentAt(mx, my);
            enforceSingleFocusedTextInput(focusedComponent);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (CategoryComponent category : categories) {
                category.overTitle(false);
                if (category.isOpened() && !category.getModules().isEmpty()) {
                    for (Component module : category.getModules()) {
                        module.mouseReleased((int)mouseX, (int)mouseY, button);
                    }
                }
            }
        }
        if (draggedCategory != null) {
            draggedCategory.dragging = false;
            draggedCategory = null;
        }
        if (pendingScaleRefresh) {
            pendingScaleRefresh = false;
            refreshLayoutForConfiguredScale();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        for (CategoryComponent category : categories) {
            category.onMouseMove(mx, my);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        int wheelInput = (int) (verticalAmount * 120);
        for (CategoryComponent category : categories) {
            category.onScroll(wheelInput, mx, my);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        FocusableTextComponent activeTextInput = getActiveFocusedTextInput();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (activeTextInput != null) {
                activeTextInput.unfocusTextInput();
                return true;
            }
            if (!binding()) {
                this.close();
                return true;
            }
        }

        if (activeTextInput != null) {
            for (CategoryComponent category : categories) {
                if (!category.isOpened() || category.getModules().isEmpty()) continue;
                for (Component module : category.getModules()) {
                    module.keyTyped((char) keyCode, keyCode);
                }
            }
            return true;
        }

        for (CategoryComponent category : categories) {
            if (category.isOpened() && !category.getModules().isEmpty()) {
                for (Component module : category.getModules()) {
                    module.keyTyped((char) keyCode, keyCode);
                }
            }
        }

        if (false) {
            String cm = this.commandLineInput.getText();
            if (keyCode == GLFW.GLFW_KEY_ENTER && !cm.isEmpty()) {
                CommandHandler.runCommand(this.commandLineInput.getText());
                this.commandLineInput.setText("");
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (false) {
            this.commandLineInput.charTyped(chr, modifiers);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        for (CategoryComponent c : categories) {
            c.dragging = false;
            c.onGuiClosed();
            for (Component m : c.getModules()) {
                m.onGuiClosed();
            }
        }
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void reloadModules() {
        categories.clear();
        isNotFirstOpen = false;
    }

    public void onSliderChange() {
        for (CategoryComponent c : categories) {
            for (ModuleComponent m : c.getModules()) {
                m.onSliderChange();
            }
        }
    }

    public void requestScaleRefresh() {
        this.pendingScaleRefresh = true;
    }

    public void refreshAfterProfileLoad() {
        MinecraftClient mc = MinecraftClient.getInstance();
        refreshLayoutForConfiguredScale();
    }

    private void refreshLayoutForConfiguredScale() {
        this.actualScreenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        this.actualScreenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        for (CategoryComponent categoryComponent : categories) {
            categoryComponent.setScreenSize(this.width, this.height);
            categoryComponent.limitPositions();
        }
    }

    private double getRenderScale() {
        return actualScreenWidth <= 0 || width <= 0 ? 1.0D : (double) actualScreenWidth / (double) width;
    }

    public static double getActiveRenderScale() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        return minecraft.currentScreen instanceof ClickGui ? ((ClickGui) minecraft.currentScreen).getRenderScale() : 1.0D;
    }

    private double getConfiguredGuiScale() {
        return 1.0;
    }

    private int toLogicalCoordinate(int coordinate) {
        return (int) Math.floor(coordinate / getRenderScale());
    }

    private boolean binding() {
        for (CategoryComponent c : categories) {
            for (ModuleComponent m : c.getModules()) {
                for (Component component : m.settings) {
                    if (component instanceof BindComponent && ((BindComponent) component).isBinding) {
                        return true;
                    }
                    if (component instanceof FocusableTextComponent && ((FocusableTextComponent) component).isTextInputFocused()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private FocusableTextComponent getActiveFocusedTextInput() {
        FocusableTextComponent activeComponent = null;
        for (CategoryComponent category : categories) {
            for (ModuleComponent module : category.getModules()) {
                for (Component component : module.settings) {
                    if (component instanceof FocusableTextComponent) {
                        FocusableTextComponent textComponent = (FocusableTextComponent) component;
                        if (textComponent.isTextInputFocused()) {
                            if (activeComponent == null) {
                                activeComponent = textComponent;
                            } else {
                                textComponent.unfocusTextInput();
                            }
                        }
                    }
                }
            }
        }
        return activeComponent;
    }

    private FocusableTextComponent findFocusedTextComponentAt(int mouseX, int mouseY) {
        List<CategoryComponent> inputOrder = new ArrayList<>(categories);
        inputOrder.sort((a, b) -> Long.compare(b.lastInteractedTime, a.lastInteractedTime));
        for (CategoryComponent category : inputOrder) {
            if (!category.isOpened() || !category.overRect(mouseX, mouseY)) continue;
            for (ModuleComponent module : category.getModules()) {
                for (Component component : module.settings) {
                    if (component instanceof FocusableTextComponent) {
                        FocusableTextComponent textComponent = (FocusableTextComponent) component;
                        if (textComponent.isTextInputFocused() && textComponent.containsClick(mouseX, mouseY)) {
                            return textComponent;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void enforceSingleFocusedTextInput(FocusableTextComponent focusedComponentToKeep) {
        for (CategoryComponent category : categories) {
            for (ModuleComponent module : category.getModules()) {
                for (Component component : module.settings) {
                    if (component instanceof FocusableTextComponent) {
                        FocusableTextComponent textComponent = (FocusableTextComponent) component;
                        if (textComponent != focusedComponentToKeep && textComponent.isTextInputFocused()) {
                            textComponent.unfocusTextInput();
                        }
                    }
                }
            }
        }
    }
}
