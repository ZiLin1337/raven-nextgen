package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PlayerListSetting extends Setting {
    private List<String> players = new ArrayList<>();
    private final int maxLength;
    private final String placeholder;

    public PlayerListSetting(String name, int maxLength, String placeholder) {
        super(name);
        this.maxLength = maxLength;
        this.placeholder = placeholder;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public void loadProfile(JsonObject data) {
        if (data.has("players")) {
            // 简单实现，实际可能需要解析JSON数组
        }
    }
}
