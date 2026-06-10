package keystrokesmod.utility.font;

import net.minecraft.client.font.TextRenderer;

public class MinecraftFontAdapter {
    public static void drawString(TextRenderer fontRenderer, String text, float x, float y, int color, boolean shadow) {
        // TextRenderer methods changed in 1.21.4
    }

    public static float getStringWidth(TextRenderer fontRenderer, String text) {
        return fontRenderer != null ? fontRenderer.getWidth(text) : 0;
    }
}