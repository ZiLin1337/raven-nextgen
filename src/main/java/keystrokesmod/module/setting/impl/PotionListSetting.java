package keystrokesmod.module.setting.impl;

import java.util.ArrayList;
import java.util.List;

public class PotionListSetting extends Setting {
    private final List<String> potions = new ArrayList<>();
    public PotionListSetting(String name) { super(name); }
    public List<String> getPotions() { return potions; }
}
