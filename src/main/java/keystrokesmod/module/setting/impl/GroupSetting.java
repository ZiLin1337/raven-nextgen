package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class GroupSetting extends Setting {
    public GroupSetting(String name) {
        super(name);
    }

    @Override
    public void loadProfile(JsonObject data) {
        // 分组设置不保存
    }
}