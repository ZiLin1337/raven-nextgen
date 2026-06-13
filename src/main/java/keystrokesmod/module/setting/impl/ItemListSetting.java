package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ItemListSetting extends Setting {
    private final List<String> items = new ArrayList<>();
    public GroupSetting group;
    
    public ItemListSetting(String name) {
        super(name);
    }
    
    public ItemListSetting(GroupSetting group, String name) {
        super(name);
        this.group = group;
    }
    
    public void addItem(String itemName) {
        if (!items.contains(itemName)) {
            items.add(itemName);
        }
    }
    
    public void removeItem(String itemName) {
        items.remove(itemName);
    }
    
    public List<String> getItems() {
        return items;
    }
    
    public List<String> getEnabledItems() {
        return new ArrayList<>(items);
    }
    
    public void clearItems() {
        items.clear();
    }
    
    public boolean contains(String itemName) {
        return items.contains(itemName);
    }
    
    @Override
    public void loadProfile(JsonObject data) {
    }
}
