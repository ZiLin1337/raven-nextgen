package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public interface RavenFontRenderer {
    
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (shadow) {
            return textRenderer.drawWithShadow(MinecraftClient.getInstance().textRenderer, text, x, y, color);
        } else {
            return textRenderer.draw(text, (int)x, (int)y, color);
        }
    }
    
    default int drawStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x, y, color, true);
    }
    
    default int getStringWidth(String text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }
    
    default int getFontHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }
}
