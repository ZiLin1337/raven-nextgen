package keystrokesmod.utility.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Font renderer interface for HUD rendering with support for glyph coloring.
 */
public interface RavenFontRenderer {
    @FunctionalInterface
    interface GlyphColorProvider {
        int colorForGlyph(char character, float xOffset, float width, Integer formattingColor);
    }

    /**
     * Draw text with optional shadow.
     */
    int drawString(String text, float x, float y, int color, boolean shadow);

    /**
     * Draw text with per-glyph coloring support.
     */
    int drawGlyphString(String text, float x, float y, GlyphColorProvider colorProvider, boolean shadow);

    default int drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    default int drawGlyphString(String text, float x, float y, GlyphColorProvider colorProvider) {
        return drawGlyphString(text, x, y, colorProvider, false);
    }

    default int drawStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x, y, color, true);
    }

    /**
     * Get the width of a string in pixels.
     */
    int getStringWidth(String text);

    /**
     * Get the font height in pixels.
     */
    int getFontHeight();

    default int getLineHeight() {
        return getFontHeight();
    }

    default int getTextTopOffset() {
        return 0;
    }

    default int getTextBottomOffset() {
        return getFontHeight();
    }

    default void destroy() {
    }
}

/**
 * Implementation of RavenFontRenderer using Minecraft's built-in TextRenderer.
 */