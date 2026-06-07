package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;

public class TextSetting extends Setting {
    private String value;
    private final int maxLength;
    
    public TextSetting(String name, String defaultValue) {
        this(name, defaultValue, 48);
    }
    
    public TextSetting(String name, String defaultValue, int maxLength) {
        super(name);
        this.value = defaultValue;
        this.maxLength = maxLength;
    }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public int getMaxLength() { return maxLength; }
    
    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName())) {
            this.value = data.get(getName()).getAsString();
        }
    }
    
    @Override
    public void saveProfile(JsonObject data) {
        data.addProperty(getName(), value);
    }
}