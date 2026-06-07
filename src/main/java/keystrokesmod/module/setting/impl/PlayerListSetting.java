package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.GroupSetting;

import java.util.ArrayList;
import java.util.List;

public class PlayerListSetting extends Setting {
    public GroupSetting group;
    private final List<String> players = new ArrayList<>();
    public PlayerListSetting(String name) { super(name); }
    public List<String> getPlayers() { return players; }
    public void addPlayer(String s) { players.add(s); }
    public void removePlayer(String s) { players.remove(s); }
    public boolean contains(String s) { return players.contains(s); }

    @Override
    public void loadProfile(com.google.gson.JsonObject json) {
        if (json.has("players")) { this.players.clear(); json.getAsJsonArray("players").forEach(e -> this.players.add(e.getAsString())); }
    }
}