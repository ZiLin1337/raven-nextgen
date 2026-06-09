package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.StringListSetting;

public class StringListComponent extends AbstractSearchListComponent {
    public final StringListSetting setting;

    public StringListComponent(StringListSetting setting, ModuleComponent moduleComponent, float o) {
        this.setting = setting;
    }
}