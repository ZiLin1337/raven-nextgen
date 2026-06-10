package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;

public abstract class AbstractTextInputComponent extends Component {
    protected final ClickTextFieldWidget textField;
    protected float xOffset = 0;

    protected AbstractTextInputComponent(ModuleComponent moduleComponent, float o, String placeholder, int maxLength) {
        this.textField = new ClickTextFieldWidget(placeholder, maxLength, 1.0f);
    }

    public boolean isFocused() {
        return textField.isFocused();
    }

    public void setFocused(boolean focused) {
        textField.setFocused(focused);
    }

    public String getText() {
        return textField.getText();
    }

    public void setXOffset(float offset) {
        this.xOffset = offset;
    }

    public abstract String getGroupName();
}