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

    default int drawWithShadow(String text, float x, float y, int color) {
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
 * Implementation of RavenFontRenderer using MinecraftClient's built-in TextRenderer.
 */
class MinecraftFontAdapter implements RavenFontRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final TextRenderer textRenderer;

    MinecraftFontAdapter(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    @Override
    public int drawString(String text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            return textRenderer.drawWithShadow(text, x, y, color);
        }
        return textRenderer.drawWithShadow(text, x, y, color);
    }

    @Override
    public int drawGlyphString(String text, float x, float y, GlyphColorProvider colorProvider, boolean shadow) {
        float currentX = x;
        Integer formattingColor = null;
        StringBuilder currentSegment = new StringBuilder();
        int segmentStartX = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i + 1 < text.length()) {
                // Section sign - formatting code
                if (currentSegment.length() > 0) {
                    int color = colorProvider.colorForGlyph('\0', segmentStartX,
                        textRenderer.getWidth(currentSegment.toString()), formattingColor);
                    if (shadow) {
                        textRenderer.drawWithShadow(currentSegment.toString(), currentX, y, color);
                    } else {
                        textRenderer.drawWithShadow(currentSegment.toString(), currentX, y, color);
                    }
                    currentX += textRenderer.getWidth(currentSegment.toString());
                    currentSegment = new StringBuilder();
                }
                i++;
                continue;
            }
            if (currentSegment.length() == 0) {
                segmentStartX = (int) (currentX - x);
            }
            currentSegment.append(c);
        }

        // Draw remaining text
        if (currentSegment.length() > 0) {
            int color = colorProvider.colorForGlyph('\0', segmentStartX,
                textRenderer.getWidth(currentSegment.toString()), formattingColor);
            if (shadow) {
                textRenderer.drawWithShadow(currentSegment.toString(), currentX, y, color);
            } else {
                textRenderer.drawWithShadow(currentSegment.toString(), currentX, y, color);
            }
            currentX += textRenderer.getWidth(currentSegment.toString());
        }

        return (int) currentX;
    }

    @Override
    public int getStringWidth(String text) {
        return textRenderer.getWidth(text);
    }

    @Override
    public int getFontHeight() {
        return textRenderer.fontHeight;
    }

    @Override
    public int getTextTopOffset() {
        return -2;
    }

    @Override
    public int getTextBottomOffset() {
        return textRenderer.fontHeight;
    }
}
