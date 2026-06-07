package keystrokesmod.clickgui.components.impl;
import org.lwjgl.glfw.GLFW;
import keystrokesmod.module.setting.impl.TextSetting;
public class TextFieldComponent extends AbstractTextInputComponent {
    public final TextSetting textSetting;

    private String valueWhenFocused;

    public TextFieldComponent(TextSetting textSetting, ModuleComponent moduleComponent, float o) {
        super(moduleComponent, o, "", 48);
        this.textSetting = textSetting;
        getTextField().setText(textSetting.getText());
    }

    @Override
    public void render() {
        if (!isTextFieldFocused()) {
            if (valueWhenFocused != null) {
                revertToSaved();
            }
            if (!getTextField().getText().equals(textSetting.getText())) {
                getTextField().setText(textSetting.getText());
            }
        }

        renderTextInput(textSetting.getName());
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!moduleComponent.isOpened || !moduleComponent.isVisible(this) || button != 0) {
            return false;
        }

        Layout layout = layout(true);
        if (isTextFieldClicked(mouseX, mouseY, layout)) {
            if (!isTextFieldFocused()) {
                valueWhenFocused = textSetting.getText();
            }
            setTextFieldFocused(true);
            return true;
        }

        if (isTextFieldFocused()) {
            revertToSaved();
            setTextFieldFocused(false);
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!moduleComponent.isOpened || !isTextFieldFocused()) {
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            revertToSaved();
            setTextFieldFocused(false);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            // submit disabled;
            valueWhenFocused = null;
            getTextField().setText(textSetting.getText());
            setTextFieldFocused(false);
            return;
        }

        if (getTextField().textboxKeyTyped(typedChar, keyCode)) {
            textSetting.setText(getTextField().getText());
        }
    }

    @Override
    public boolean isBaseVisible() {
        return textSetting.visible;
    }

    @Override
    public String getGroupName() {
        return null /* group disabled */ != null ? null /* group disabled */.getName() : "";
    }

    public boolean containsClick(int mouseX, int mouseY) {
        return isTextFieldClicked(mouseX, mouseY, layout(true));
    }

    private void revertToSaved() {
        if (valueWhenFocused != null) {
            getTextField().setText(valueWhenFocused);
            textSetting.setText(valueWhenFocused);
            valueWhenFocused = null;
        }
    }
}