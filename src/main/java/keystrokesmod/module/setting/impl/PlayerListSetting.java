package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PlayerListSetting extends Setting {
    private final List<String> players = new ArrayList<>();
    public GroupSetting group;
    
    public PlayerListSetting(String name) {
        super(name);
    }
    
    public PlayerListSetting(GroupSetting group, String name) {
        super(name);
        this.group = group;
    }
    
    public void addPlayer(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
    }
    
    public void removePlayer(String playerName) {
        players.remove(playerName);
    }
    
    public List<String> getPlayers() {
        return players;
    }
    
    public List<String> getEnabledPlayers() {
        return new ArrayList<>(players);
    }
    
    public void clearPlayers() {
        players.clear();
    }
    
    public boolean contains(String playerName) {
        return players.contains(playerName);
    }
    
    @Override
    public void loadProfile(JsonObject data) {
    }
}
