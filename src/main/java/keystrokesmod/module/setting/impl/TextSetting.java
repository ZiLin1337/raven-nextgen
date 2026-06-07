package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;

public class TextSetting extends Setting {
    private String value;
    
    public TextSetting(String name, String defaultValue) {
        super(name);
        this.value = defaultValue;
    }
    
    public TextSetting(String name, String defaultValue, String description) {
        super(name);
        this.value = defaultValue;
    }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getText() { return value; }
    public void setText(String text) { this.value = text; }
    
    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName())) {
            this.value = data.get(getName()).getAsString();
        }
    }
}
