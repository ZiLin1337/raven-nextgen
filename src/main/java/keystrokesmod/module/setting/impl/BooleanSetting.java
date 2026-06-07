package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;

public class BooleanSetting extends Setting {
    private boolean value;
    
    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }
    
    public boolean isEnabled() { return value; }
    public void setEnabled(boolean value) { this.value = value; }
    public void toggle() { value = !value; }
    
    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName())) {
            this.value = data.get(getName()).getAsBoolean();
        }
    }
    
    @Override
    public void saveProfile(JsonObject data) {
        data.addProperty(getName(), value);
    }
}