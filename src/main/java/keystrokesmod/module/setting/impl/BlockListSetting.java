package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class BlockListSetting extends Setting {
    public BlockListSetting(String name) {
        super(name);
    }

    public BlockListSetting(GroupSetting group, String name) {
        super(name);
    }

    @Override
    public void loadProfile(JsonObject data) {
    }
}