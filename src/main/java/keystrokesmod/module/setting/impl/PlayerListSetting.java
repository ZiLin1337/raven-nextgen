package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PlayerListSetting extends Setting {
    public GroupSetting group;
    public boolean visible = true;
    public List<String> players = new ArrayList<>();
    
    public PlayerListSetting(String name) {
        super(name);
    }
    
    public List<String> getEntries() {
        return players;
    }
    
    public boolean addPlayer(String name) {
        return players.add(name);
    }
    
    public void removePlayer(String name) {
        players.remove(name);
    }
    
    @Override
    public void loadProfile(JsonObject data) {}
}
