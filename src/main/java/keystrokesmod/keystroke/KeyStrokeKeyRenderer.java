package keystrokesmod.keystroke;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class KeyStrokeKeyRenderer {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private int x, y;
    private String key;
    private int keyCode;

    public KeyStrokeKeyRenderer(int x, int y, String key, int keyCode) {
        this.x = x;
        this.y = y;
        this.key = key;
        this.keyCode = keyCode;
    }

    public void render(DrawContext context) {
        boolean pressed = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) == 1;
        int bgColor = pressed ? 0xFF00AA00 : 0x80000000;
        int textColor = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;

        context.fill(x, y, x + 14, y + 14, bgColor);
        context.drawText(MinecraftClient.getInstance().textRenderer, key, x + 3, y + 3, textColor, true);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
