package keystrokesmod.keystroke;

import keystrokesmod.helper.MouseHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class KeyStrokeMouse {
    private static MinecraftClient mc = mc;
    private int x, y;

    public KeyStrokeMouse(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context) {
        int lcps = MouseHelper.getLeftCPS();
        int rcps = MouseHelper.getRightCPS();

        context.fill(x, y, x + 28, y + 14, 0x80000000);
        context.drawText(mc.textRenderer, "L" + lcps, x + 2, y + 2, 0xFF00FF00, true);

        context.fill(x, y + 16, x + 28, y + 30, 0x80000000);
        context.drawText(mc.textRenderer, "R" + rcps, x + 2, y + 18, 0xFF00FF00, true);
    }
}
