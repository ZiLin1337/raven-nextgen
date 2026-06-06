package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class StringListSetting extends Setting {
    private final List<String> strings = new ArrayList<>();
    public StringListSetting(String name) { super(name); }
    public List<String> getStrings() { return strings; }
}
