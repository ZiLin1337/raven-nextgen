package keystrokesmod.module.setting.impl;

import java.util.ArrayList;
import java.util.List;

public class ItemListSetting extends BlockListSetting {
    private final List<String> items = new ArrayList<>();

    public ItemListSetting(String name, String... legacyProfileKeys) {
        super(name);
    }

    public ItemListSetting(GroupSetting group, String name, String... legacyProfileKeys) {
        super(group, name);
    }

    public void addItem(String id) {
        items.add(id);
    }

    public void removeItem(String id) {
        items.remove(id);
    }

    public List<String> getItems() {
        return items;
    }

    public boolean containsItem(String id) {
        return items.contains(id);
    }
}