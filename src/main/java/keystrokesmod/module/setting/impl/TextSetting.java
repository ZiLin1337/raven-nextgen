package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.Setting;

public class TextSetting extends Setting {
    private String value = "";
    private final int maxLength;

    public TextSetting(String name, String value, String placeholder, int maxLength, Runnable callback) {
        this(name, maxLength);
        this.value = value;
    }
    public TextSetting(String name, int maxLength) {
        super(name);
        this.maxLength = maxLength;
    }

    public String getText() { return value; }
    public void setText(String value) { setValue(value); }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value.length() <= maxLength) {
            this.value = value;
        }
    }

    @Override
    public void loadProfile(JsonObject data) {
        if (data.has("value")) {
            this.value = data.get("value").getAsString();
        }
    }
}
