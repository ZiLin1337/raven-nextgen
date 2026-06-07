package keystrokesmod.utility.font;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
public interface RavenFontRenderer {
    default int drawString(String text, float x, float y, int color, boolean shadow) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return 0;
        try {
            if (shadow) {
                return mc.textRenderer.draw(text, x, y, color, true);
            } else {
                return mc.textRenderer.draw(text, x, y, color, false);
            }
        } catch (Exception e) { return 0; }
    }
    default int drawStringWithShadow(String text, float x, float y, int color) { return drawString(text, x, y, color, true); }
    default int getStringWidth(String text) { MinecraftClient mc = MinecraftClient.getInstance(); return mc.textRenderer != null ? mc.textRenderer.getWidth(text) : 0; }
    default int getFontHeight() { MinecraftClient mc = MinecraftClient.getInstance(); return mc.textRenderer != null ? mc.textRenderer.fontHeight : 9; }
    default int getTextTopOffset() { return 2; }
    default int getTextBottomOffset() { return 2; }
}