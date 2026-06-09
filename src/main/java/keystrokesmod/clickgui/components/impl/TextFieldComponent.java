package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.TextSetting;

public class TextFieldComponent extends AbstractTextInputComponent {
    public final TextSetting textSetting;

    public TextFieldComponent(TextSetting textSetting, ModuleComponent moduleComponent, float o) {
        super(moduleComponent, o, "", 32);
        this.textSetting = textSetting;
    }

    @Override
    public String getGroupName() {
        return "";
    }
}