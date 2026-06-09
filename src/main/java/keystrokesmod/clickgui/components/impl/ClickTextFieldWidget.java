package keystrokesmod.clickgui.components.impl;

public class ClickTextFieldWidget {
    private String text = "";
    private boolean focused;

    public ClickTextFieldWidget(String placeholder, int maxLength, float scale) {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        return false;
    }

    public void tickCursor() {
    }

    public void render(float left, float top, float right, float bottom) {
    }

    public boolean contains(int mouseX, int mouseY, float left, float top, float right, float bottom) {
        return false;
    }
}