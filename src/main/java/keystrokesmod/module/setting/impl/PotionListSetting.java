package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PotionListSetting extends Setting {
    private final List<String> potions = new ArrayList<>();
    public GroupSetting group;
    
    public PotionListSetting(String name) {
        super(name);
    }
    
    public PotionListSetting(GroupSetting group, String name) {
        super(name);
        this.group = group;
    }
    
    public void addPotion(String potionName) {
        if (!potions.contains(potionName)) {
            potions.add(potionName);
        }
    }
    
    public void removePotion(String potionName) {
        potions.remove(potionName);
    }
    
    public List<String> getPotions() {
        return potions;
    }
    
    public List<String> getEnabledPotions() {
        return new ArrayList<>(potions);
    }
    
    public void clearPotions() {
        potions.clear();
    }
    
    public boolean contains(String potionName) {
        return potions.contains(potionName);
    }
    
    @Override
    public void loadProfile(JsonObject data) {
    }
}
