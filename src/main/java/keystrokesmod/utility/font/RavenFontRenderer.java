package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Font renderer interface for 1.21.4.
 * Note: In 1.21.4, drawWithShadow is on DrawContext, not TextRenderer.
 * The draw methods here return 0 as placeholder - use DrawContext for actual rendering.
 */
public interface RavenFontRenderer {
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        // In 1.21.4, text rendering requires DrawContext
        // This is a placeholder that returns 0
        // Actual rendering should use DrawContext.drawTextWithShadow()
        return 0;
    }
    default int drawStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x, y, color, true);
    }
    default int getStringWidth(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.textRenderer != null ? mc.textRenderer.getWidth(text) : 0;
    }
    default int getFontHeight() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.textRenderer != null ? mc.textRenderer.fontHeight : 9;
    }
    default int getTextTopOffset() { return 2; }
    default int getTextBottomOffset() { return 2; }
}
