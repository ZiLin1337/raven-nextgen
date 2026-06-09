package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.PlayerListSetting;

public class PlayerListComponent extends AbstractSearchListComponent {
    public final PlayerListSetting setting;

    public PlayerListComponent(PlayerListSetting setting, ModuleComponent moduleComponent, float o) {
        this.setting = setting;
    }
}