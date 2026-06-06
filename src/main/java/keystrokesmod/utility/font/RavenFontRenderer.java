package keystrokesmod.utility.font;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;

/**
 * Font renderer wrapper - simplified for 1.21.4 Fabric
 * Original used TextRenderer.drawWithShadow which no longer exists
 */
public interface RavenFontRenderer {
    
    default int drawStringWithShadow(String text, float x, float y, int color) {
        // In 1.21.4, TextRenderer API changed significantly
        // For now return 0 to compile; rendering will need DrawContext later
        return 0;
    }
    
    default int getStringWidth(String text) {
        if (text == null) return 0;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        return textRenderer.getWidth(text);
    }
    
    default int getFontHeight() {
        return 9;
    }
}
