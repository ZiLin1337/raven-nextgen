package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public interface RavenFontRenderer {
    
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (shadow) {
            // 使用正确的1.21.4 API: drawWithShadow(Text, float, float, int)
            return textRenderer.drawWithShadow(Text.literal(text), x, y, color);
        } else {
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
