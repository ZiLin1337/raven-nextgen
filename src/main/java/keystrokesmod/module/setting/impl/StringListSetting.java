package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    private List<String> strings = new ArrayList<>();
    private final int maxLength;
    private final String placeholder;

    public StringListSetting(String name, int maxLength, String placeholder) {
        super(name);
        this.maxLength = maxLength;
        this.placeholder = placeholder;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public void loadProfile(JsonObject data) {
        if (data.has("strings")) {
            // 简单实现
        }
    }
}
