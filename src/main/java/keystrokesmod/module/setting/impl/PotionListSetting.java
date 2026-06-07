package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class PotionListSetting extends Setting {
    private final List<String> potions = new ArrayList<>();
    public PotionListSetting(String name) { super(name); }
    public List<String> getPotions() { return potions; }
    public void addPotion(String name) { if (!potions.contains(name)) potions.add(name); }
    public void removePotion(String name) { potions.remove(name); }
    public boolean containsPotion(String name) { return potions.contains(name); }
    @Override public void loadProfile(JsonObject data) { if (data == null || !data.has(getName())) return; potions.clear(); if (data.get(getName()).isJsonArray()) { for (JsonElement e : data.getAsJsonArray(getName())) potions.add(e.getAsString()); } }
    public void saveProfile(JsonObject data) { com.google.gson.JsonArray arr = new com.google.gson.JsonArray(); for (String p : potions) arr.add(p); data.add(getName(), arr); }
}
