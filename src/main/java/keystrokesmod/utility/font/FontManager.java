package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class FontManager implements keystrokesmod.utility.IMinecraftInstance {
    private static boolean initialized = false;
    
    public static void init() {
        if (initialized) return;
        initialized = true;
    }
    
    public static TextRenderer getDefaultFont() {
        return MinecraftClient.getInstance().textRenderer;
    }
    
    public static void drawString(String text, float x, float y, int color, boolean shadow) {
        // Stub - needs DrawContext to work properly
    }
    
    public static float getStringWidth(String text) {
        TextRenderer font = getDefaultFont();
        return font != null ? font.getWidth(text) : 0;
    }
    
    public static int getFontHeight() {
        return 9;
    }
}
