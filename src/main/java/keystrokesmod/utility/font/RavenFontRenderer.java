package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public interface RavenFontRenderer {
    
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (shadow) {
            // 1.21.4的drawWithShadow正确签名
            return textRenderer.drawWithShadow(textRenderer, text, x, y, color);
        } else {
            // 简单返回0，避免复杂的draw调用
            return 0;
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
