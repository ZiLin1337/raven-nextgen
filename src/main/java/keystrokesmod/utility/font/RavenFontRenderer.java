package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public interface RavenFontRenderer {
    
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            // 1.21.4 的正确签名: drawWithShadow(Text, float, float, int)
            return MinecraftClient.getInstance().textRenderer.drawWithShadow(Text.literal(text), x, y, color);
        } else {
            // 非阴影绘制暂时返回0
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
