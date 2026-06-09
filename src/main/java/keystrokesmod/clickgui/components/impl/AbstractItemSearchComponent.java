package keystrokesmod.clickgui.components.impl;

import keystrokesmod.module.setting.impl.ItemListSetting;

public abstract class AbstractItemSearchComponent<T extends ItemListSetting> extends AbstractSearchListComponent {
    protected final T setting;

    protected AbstractItemSearchComponent(T setting, ModuleComponent moduleComponent, float o) {
        this.setting = setting;
    }
}