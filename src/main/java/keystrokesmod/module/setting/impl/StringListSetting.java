package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    public boolean visible = true;
    public GroupSetting group;

    public StringListSetting(String name) {
        super(name);
    }

    public List<String> getEntries() {
        return new ArrayList<>();
    }

    public boolean addEntry(String value) {
        return false;
    }

    public void removeEntry(String value) {
    }

    @Override
    public void loadProfile(JsonObject data) {
    }
}