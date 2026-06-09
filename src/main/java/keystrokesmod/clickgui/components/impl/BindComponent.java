package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.setting.impl.KeySetting;

public class BindComponent extends Component {
    public final KeySetting keySetting;
    public float xOffset;
    public BindComponent(ModuleComponent moduleComponent, KeySetting keySetting, float o) {
        this.keySetting = keySetting;
    }
    public BindComponent(ModuleComponent moduleComponent, float o) {
        this.keySetting = null;
    }
}
