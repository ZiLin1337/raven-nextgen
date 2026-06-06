package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class DescriptionSetting extends Setting {
    private String description;

    public DescriptionSetting(String description) {
        super(description);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void loadProfile(JsonObject data) {
        // 描述设置不保存
    }
}