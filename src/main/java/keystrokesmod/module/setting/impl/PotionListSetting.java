package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PotionListSetting extends Setting {
    private List<String> potions = new ArrayList<>();
    private final int maxLength;
    private final String placeholder;

    public PotionListSetting(String name, int maxLength, String placeholder) {
        super(name);
        this.maxLength = maxLength;
        this.placeholder = placeholder;
    }

    public List<String> getPotions() {
        return potions;
    }

    public void setPotions(List<String> potions) {
        this.potions = potions;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public void loadProfile(JsonObject data) {
        if (data.has("potions")) {
            // 简单实现
        }
    }
}
