package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.utility.PlayerRelationsManager;
import java.util.ArrayList;
import java.util.List;

public class PlayerListSetting extends Setting {
    public GroupSetting group;
    public boolean visible = true;
    public PlayerListSetting(String name) { super(name); }
    public List<PlayerRelationsManager.PlayerEntry> getEntries() { return new ArrayList<>(); }
    public boolean addPlayer(String name) { return false; }
    public void removePlayer(String name) {}
    @Override public void loadProfile(JsonObject data) {}
}
