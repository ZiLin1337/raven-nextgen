package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.PotionListSetting;

public class PotionSearchComponent extends AbstractSearchListComponent {
    public final PotionListSetting setting;

    public PotionSearchComponent(PotionListSetting setting, ModuleComponent moduleComponent, float o) {
        this.setting = setting;
    }
}