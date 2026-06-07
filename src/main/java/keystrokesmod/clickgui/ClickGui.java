package keystrokesmod.clickgui;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.components.impl.CategoryComponent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClickGui extends Screen {
    public static double getActiveRenderScale() { return 1.0; }
    public static ArrayList<CategoryComponent> categories;
    private CategoryComponent draggedCategory;
    private static boolean isInitialized;

    public ClickGui() {
        super(Text.literal("Raven ClickGUI"));
        categories = new ArrayList<>();
    }

    private void initCategories() {
        if (isInitialized) return;
        isInitialized = true;
        categories.clear();
        
        int x = 5;
        int y = 5;
        for (Module.category c : Module.category.values()) {
            CategoryComponent categoryComponent = new CategoryComponent(c);
            categoryComponent.x = x;
            categoryComponent.y = y;
            categories.add(categoryComponent);
            x += 105;
            if (x + 100 > width) {
                x = 5;
                y += 20;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        initCategories();
        
        // Semi-transparent background
        context.fill(0, 0, width, height, 0x80000000);
        
        // Draw watermark
        String watermark = "Raven bS [1.21.4]";
        int wmColor = Utils.getChroma(2L, 0L);
        context.drawTextWithShadow(textRenderer, watermark, 2, 2, wmColor);
        
        // Render categories (sorted by last interaction for Z-ordering)
        List<CategoryComponent> sorted = new ArrayList<>(categories);
        sorted.sort(Comparator.comparingLong(c -> c.lastInteractedTime));
        
        for (CategoryComponent category : sorted) {
            category.render();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        
        // Find topmost category under cursor
        List<CategoryComponent> sorted = new ArrayList<>(categories);
        sorted.sort((a, b) -> Long.compare(b.lastInteractedTime, a.lastInteractedTime));
        
        for (CategoryComponent category : sorted) {
            if (category.isMouseOver(mx, my)) {
                category.onClick(mx, my, button);
                draggedCategory = category.dragging ? category : null;
                break;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggedCategory != null) {
            draggedCategory.onMouseRelease();
            draggedCategory = null;
        }
        for (CategoryComponent category : categories) {
            category.onMouseRelease();
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
        // TODO: Handle scroll for settings sliders
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        for (CategoryComponent c : categories) {
            c.onMouseRelease();
        }
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /** Reload modules in each category */
    public void reloadModules() {
        categories.clear();
        isInitialized = false;
    }
}
