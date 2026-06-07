package keystrokesmod.module.setting.impl;

import keystrokesmod.module.setting.Setting;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    private final List<String> strings = new ArrayList<>();
    
    public StringListSetting(String name) { 
        super(name); 
    }
    
    public List<String> getStrings() { return strings; }
    public List<String> getEntries() { return strings; }
    public boolean addEntry(String entry) { 
        if (!strings.contains(entry)) {
            strings.add(entry);
            return true;
        }
        return false;
    }
    public void removeEntry(String entry) { strings.remove(entry); }
    public int getMaxLength() { return 48; }
    public String getPlaceholder() { return ""; }
    
    @Override
    public void loadProfile(JsonObject data) {
        strings.clear();
        if (data.has(getName()) && data.get(getName()).isJsonArray()) {
            for (var e : data.getAsJsonArray(getName())) {
                strings.add(e.getAsString());
            }
        }
    }
}
