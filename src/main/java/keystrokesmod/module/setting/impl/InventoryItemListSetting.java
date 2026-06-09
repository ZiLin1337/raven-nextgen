package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;

public class InventoryItemListSetting extends ItemListSetting {
    public InventoryItemListSetting(String name) {
        super(name);
    }

    public InventoryItemListSetting(GroupSetting group, String name) {
        super(group, name);
    }

    @Override
    public void loadProfile(JsonObject data) {
    }
}