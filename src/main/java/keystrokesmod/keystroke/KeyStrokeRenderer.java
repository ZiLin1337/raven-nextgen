package keystrokesmod.keystroke;

import keystrokesmod.Raven;
import keystrokesmod.helper.MouseHelper;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class KeyStrokeRenderer {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private int x, y;

    public KeyStrokeRenderer(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context) {
        int yOff = y;
        context.fill(x, yOff, x + 30, yOff + 20, 0x80000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, "W", x + 10, yOff + 5, 0xFFFFFFFF, true);
        yOff += 22;

        context.fill(x, yOff, x + 14, yOff + 14, 0x80000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, "A", x + 3, yOff + 2, 0xFFFFFFFF, true);
        context.fill(x + 16, yOff, x + 30, yOff + 14, 0x80000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, "S", x + 19, yOff + 2, 0xFFFFFFFF, true);
        yOff += 16;

        context.fill(x + 8, yOff, x + 22, yOff + 14, 0x80000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, "D", x + 12, yOff + 2, 0xFFFFFFFF, true);
        yOff += 18;

        int cps = MouseHelper.getLeftCPS();
        context.fill(x, yOff, x + 14, yOff + 14, 0x80000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, String.valueOf(cps), x + 2, yOff + 2, 0xFF00FF00, true);
        context.fill(x + 16, yOff, x + 30, yOff + 14, 0x80000000);
        int rcps = MouseHelper.getRightCPS();
        context.drawText(MinecraftClient.getInstance().textRenderer, String.valueOf(rcps), x + 18, yOff + 2, 0xFF00FF00, true);
    }
}
