package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.BlockListSetting;

public class BlockSearchComponent extends AbstractSearchListComponent {
    public final BlockListSetting setting;

    public BlockSearchComponent(BlockListSetting setting, ModuleComponent moduleComponent, float o) {
        this.setting = setting;
    }
}