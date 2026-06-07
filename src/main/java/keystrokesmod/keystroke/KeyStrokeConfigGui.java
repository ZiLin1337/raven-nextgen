package keystrokesmod.keystroke;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import keystrokesmod.Raven;

public class KeyStrokeConfigGui extends Screen {
    private int x, y;

    public KeyStrokeConfigGui() {
        super(Text.literal("KeyStroke Config"));
        this.x = 30;
        this.y = 30;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x80000000);
        context.drawTextWithShadow(textRenderer, "KeyStroke Configuration", 10, 10, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Drag to reposition, ESC to save", 10, 30, 0xFFAAAAAA);

        keystroke.KeyStrokeRenderer renderer = new keystroke.KeyStrokeRenderer(x, y);
        renderer.render(context);

        if (mouseX >= x && mouseX <= x + 30 && mouseY >= y && mouseY <= y + 100)) {
            if (isDragging)) {
                x = mouseX - dragOffX;
                y = mouseY - dragOffY;
            }
        }

        context.drawTextWithShadow(textRenderer, "Position: " + x + ", " + y, 10, 50, 0xFF888888);
    }

    private boolean isDragging = false;
    private int dragOffX, dragOffY;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + 30 && mouseY >= y && mouseY <= y + 100)) {
            isDragging = true;
            dragOffX = (int)mouseX - x;
            dragOffY = (int)mouseY - y;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) close();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
