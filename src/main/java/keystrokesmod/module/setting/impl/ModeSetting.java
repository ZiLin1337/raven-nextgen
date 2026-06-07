package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;

public class ModeSetting extends Setting {
    private final String[] modes;
    private int selectedIndex;
    
    public ModeSetting(String name, String[] modes, int defaultIndex) {
        super(name);
        this.modes = modes;
        this.selectedIndex = defaultIndex;
    }
    
    public String getMode() { return modes[selectedIndex]; }
    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int index) { this.selectedIndex = index; }
    public String[] getModes() { return modes; }
    
    public void setMode(String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(mode)) {
                selectedIndex = i;
                return;
            }
        }
    }
    
    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName())) {
            setMode(data.get(getName()).getAsString());
        }
    }
    
    @Override
    public void saveProfile(JsonObject data) {
        data.addProperty(getName(), getMode());
    }
}