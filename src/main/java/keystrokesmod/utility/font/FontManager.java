package keystrokesmod.utility.font;

public class FontManager {
    private static GlyphFontRenderer fontRenderer;

    public static GlyphFontRenderer getFontRenderer() {
        if (fontRenderer == null) {
            fontRenderer = new GlyphFontRenderer();
        }
        return fontRenderer;
    }

    public static void init() {
    }
}