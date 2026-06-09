package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class GroupSetting extends Setting {
    private boolean opened;
    public GroupSetting(String name) {
        super(name);
    }

    public boolean isOpened() { return opened; }
    public void setOpened(boolean opened) { this.opened = opened; }
    @Override
    public void loadProfile(JsonObject data) {
        // 分组设置不保存
    }
}