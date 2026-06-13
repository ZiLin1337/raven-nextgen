package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    private final List<String> strings = new ArrayList<>();
    public GroupSetting group;
    
    public StringListSetting(String name) {
        super(name);
    }
    
    public StringListSetting(GroupSetting group, String name) {
        super(name);
        this.group = group;
    }
    
    public void addString(String str) {
        if (!strings.contains(str)) {
            strings.add(str);
        }
    }
    
    public void removeString(String str) {
        strings.remove(str);
    }
    
    public List<String> getStrings() {
        return strings;
    }
    
    public List<String> getEnabledStrings() {
        return new ArrayList<>(strings);
    }
    
    public void clearStrings() {
        strings.clear();
    }
    
    public boolean contains(String str) {
        return strings.contains(str);
    }
    
    @Override
    public void loadProfile(JsonObject data) {
    }
}
