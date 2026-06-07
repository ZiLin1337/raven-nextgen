package keystrokesmod.module.setting.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import keystrokesmod.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public class BlockListSetting extends Setting {
    private final List<String> blocks = new ArrayList<>();
    private final String[] legacyProfileKeys;
    public GroupSetting group;

    public BlockListSetting(String name) { this(name, new String[0]); }
    public BlockListSetting(String name, String... legacyProfileKeys) {
        super(name);
        this.legacyProfileKeys = legacyProfileKeys != null ? legacyProfileKeys : new String[0];
    }
    public BlockListSetting(GroupSetting group, String name) { super(name); this.group = group; }
    public BlockListSetting(GroupSetting group, String name, String... legacyProfileKeys) {
        super(name);
        this.group = group;
        this.legacyProfileKeys = legacyProfileKeys != null ? legacyProfileKeys : new String[0];
    }
    public void addBlock(String r) { if (!blocks.contains(r)) blocks.add(r); }
    public void removeBlock(String r) { blocks.remove(r); }
    public List<String> getBlocks() { return blocks; }
    public boolean contains(String s) {
        if (blocks.contains(s)) return true;
        String r = extractRegistryId(s);
        return r != null && blocks.contains(r + ":*");
    }
    @Override public String getProfileKey() { return group == null ? getName() : group.getName() + "." + getName(); }
    private static String extractRegistryId(String s) {
        if (s == null || s.isEmpty()) return null;
        if (s.endsWith(":*")) return s.substring(0, s.length() - 2);
        String[] p = s.split(":");
        if (p.length >= 3) return p[0] + ":" + p[1];
        if (p.length == 2) return s;
        return null;
    }
    @Override public void loadProfile(JsonObject data) {
        String key = null;
        if (data.has(getProfileKey())) key = getProfileKey();
        else if (data.has(getName())) key = getName();
        else { for (String k : legacyProfileKeys) { if (data.has(k)) { key = k; break; } } }
        if (key == null) return;
        blocks.clear();
        JsonElement el = data.get(key);
        if (el.isJsonArray()) { for (JsonElement e : el.getAsJsonArray()) blocks.add(e.getAsString()); }
    }
    public JsonArray toJsonArray() {
        JsonArray arr = new JsonArray();
        for (String b : blocks) arr.add(new JsonPrimitive(b));
        return arr;
    }
    protected String[] getLegacyProfileKeys() { return legacyProfileKeys; }
}
