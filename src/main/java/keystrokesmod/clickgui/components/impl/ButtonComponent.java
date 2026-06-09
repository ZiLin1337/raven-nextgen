package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class ButtonComponent extends Component {
    public ButtonSetting buttonSetting;
    public float xOffset = 0;

    public ButtonComponent(Module mod, ButtonSetting setting, ModuleComponent moduleComponent, float o) {
        this.buttonSetting = setting;
    }
}