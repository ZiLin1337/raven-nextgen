package keystrokesmod.module.setting.impl;

import com.google.gson.*;
import java.util.*;

public class InventoryItemListSetting extends ItemListSetting {
    private static final int DEFAULT_SLOT = 1;
    private final Map<String, Integer> assignedSlots = new HashMap<>();

    public InventoryItemListSetting(String name) { super(name); }
    public InventoryItemListSetting(String name, String... legacyProfileKeys) { super(name, legacyProfileKeys); }
    public InventoryItemListSetting(GroupSetting group, String name) { super(group, name); }
    public InventoryItemListSetting(GroupSetting group, String name, String... legacyProfileKeys) { super(group, name, legacyProfileKeys); }

    @Override public void addItem(String id) {
        if (id == null || id.isEmpty() || containsItem(id)) return;
        super.addItem(id);
        assignedSlots.put(id, DEFAULT_SLOT);
    }
    @Override public void removeItem(String id) { super.removeItem(id); assignedSlots.remove(id); }
    public Integer getAssignedSlot(String id) {
        if (id == null || !getItems().contains(id)) return null;
        return assignedSlots.getOrDefault(id, DEFAULT_SLOT);
    }
    public void setAssignedSlot(String id, Integer slot) {
        if (id == null || !getItems().contains(id)) return;
        assignedSlots.put(id, slot == null || slot < 1 || slot > 9 ? DEFAULT_SLOT : slot);
    }
    @Override public void loadProfile(JsonObject data) {
        String key = null;
        if (data.has(getProfileKey())) key = getProfileKey();
        else if (data.has(getName())) key = getName();
        else { for (String k : getLegacyProfileKeys() { if (data.has(k) { key = k; break; } } }
        if (key == null) return;
        getItems().clear(); assignedSlots.clear();
        JsonElement el = data.get(key);
        if (!el.isJsonArray()) return;
        for (JsonElement e : el.getAsJsonArray()) {
            if (e == null || e.isJsonNull()) continue;
            if (e.isJsonPrimitive()) {
                String sid = e.getAsString();
                if (sid == null || sid.isEmpty() || containsItem(sid)) continue;
                super.addItem(sid); assignedSlots.put(sid, DEFAULT_SLOT);
            } else if (e.isJsonObject() {
                JsonObject o = e.getAsJsonObject();
                if (!o.has("id")) continue;
                String sid = o.get("id").getAsString();
                if (sid == null || sid.isEmpty() || containsItem(sid)) continue;
                super.addItem(sid);
                int slot = DEFAULT_SLOT;
                if (o.has("slot") && o.get("slot").isJsonPrimitive()) {
                    int cs = o.get("slot").getAsInt();
                    if (cs >= 1 && cs <= 9) slot = cs;
                }
                assignedSlots.put(sid, slot);
            }
        }
    }
    @Override public JsonArray toJsonArray() {
        JsonArray arr = new JsonArray();
        for (String sid : getItems()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", sid);
            o.add("slot", new JsonPrimitive(getAssignedSlot(sid));
            arr.add(o);
        }
        return arr;
    }
}
