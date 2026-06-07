package keystrokesmod.clickgui.components.impl;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.font.RavenFontRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class ClickGuiTextField {
    private static final float DEFAULT_TEXT_SCALE = 0.5f;
    private static final int BACKGROUND_COLOR = 0xFF222222;
    private static final int FOCUSED_BACKGROUND_COLOR = 0xFF2A2A2A;
    private static final int OUTLINE_COLOR = 0xFF3A3A3A;
    private static final int FOCUSED_OUTLINE_COLOR = 0xFF555500;
    private static int nextId;
    private final TextFieldWidget textField;
    private final String placeholder;
    private final float textScale;
    private long lastCursorTick;
    private boolean cursorVisible;

    public ClickGuiTextField(String placeholder, int maxLength) {
        this(placeholder, maxLength, DEFAULT_TEXT_SCALE);
    }

    public ClickGuiTextField(String placeholder, int maxLength, float textScale) {
        this.placeholder = placeholder == null ? "" : placeholder;
        this.textScale = textScale;
        MinecraftClient mc = MinecraftClient.getInstance();
        this.textField = new TextFieldWidget(nextId++, mc.textRenderer, 0, 0, 100, 20, Text.literal(""));
        this.textField.setMaxLength(maxLength);
    }

    public void render(float left, float top, float right, float bottom) {
        float textLeft = left + 2.0f;
        float width = Math.max(0.0f, right - left - 4.0f);
        float backgroundBottom = bottom - 1.0f;
        float height = Math.max(0.0f, backgroundBottom - top);
        RavenFontRenderer renderer = Gui.getClickGuiSettingFontRenderer();
        String fullText = textField.getText();
        String visibleText = getVisibleText(fullText, 0, width, renderer);
        float textY = centeredScaledTextY(top, height, renderer);
        RenderUtils.drawRect(left, top, right, backgroundBottom, isFocused() ? FOCUSED_BACKGROUND_COLOR : BACKGROUND_COLOR);
        RenderUtils.drawOutline(left, top, right, backgroundBottom, 1.0f, isFocused() ? FOCUSED_OUTLINE_COLOR : OUTLINE_COLOR);
        if (!visibleText.isEmpty()) {
            drawScaledText(visibleText, textLeft, textY, 0xE0E0E0, renderer);
        } else if (!textField.isFocused() && !placeholder.isEmpty()) {
            drawScaledText("\u00A77" + placeholder, textLeft, textY, 0xAAAAAA, renderer);
        }
        if (textField.isFocused() && cursorVisible) {
            float cursorX = textLeft + renderer.getStringWidth(visibleText) * textScale;
            RenderUtils.drawRect(cursorX, top + 2.0f, cursorX + 1.0f, bottom - 2.0f, 0xFFD0D0D0);
        }
    }

    public void tickCursor() {
        if (System.currentTimeMillis() - lastCursorTick >= 500L) {
            lastCursorTick = System.currentTimeMillis();
            cursorVisible = !cursorVisible;
        }
    }

    public boolean contains(int mouseX, int mouseY, float left, float top, float right, float bottom) {
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        return textField.charTyped(typedChar, keyCode);
    }

    public String getText() { return textField.getText(); }
    public void setText(String text) { textField.setText(text == null ? "" : text); }
    public boolean isFocused() { return textField.isFocused(); }
    public void setFocused(boolean focused) {
        textField.setFocused(focused);
        if (focused) { cursorVisible = true; lastCursorTick = System.currentTimeMillis(); }
    }

    private String getVisibleText(String text, int start, float width, RavenFontRenderer r) {
        if (text == null || text.isEmpty() || width <= 0) return "";
        String remaining = text.substring(start);
        StringBuilder visible = new StringBuilder();
        for (int i = 0; i < remaining.length(); i++) {
            String candidate = visible.toString() + remaining.charAt(i);
            if (r.getStringWidth(candidate) > width / textScale) break;
            visible.append(remaining.charAt(i));
        }
        return visible.toString();
    }

    private float centeredScaledTextY(float top, float height, RavenFontRenderer r) {
        float h = Math.max(1.0f, (r.getTextBottomOffset() - r.getTextTopOffset()) * textScale);
        return top + (height - h) / 2.0f - r.getTextTopOffset() * textScale;
    }

    private void drawScaledText(String text, float x, float y, int color, RavenFontRenderer r) {
        GL11.glPushMatrix();
        GL11.glScaled(textScale, textScale, textScale);
        r.drawString(text, x / textScale, y / textScale, color, false);
        GL11.glPopMatrix();
    }
}
