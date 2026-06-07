package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    private final List<String> strings = new ArrayList<>();
    public StringListSetting(String name) { super(name);     @Override public void loadProfile(com.google.gson.JsonObject data) {}
}
    public List<String> getStrings() { return strings;     @Override public void loadProfile(com.google.gson.JsonObject data) {}
}
    @Override public void loadProfile(com.google.gson.JsonObject data) {}
}
