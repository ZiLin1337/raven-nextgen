package keystrokesmod.module.setting;

import com.google.gson.JsonObject;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;

public abstract class Setting {
    public String name;
    public boolean visible = true;

    public Setting(String name) {
        this.name = name;
    }

    public void setVisible(boolean visible, Module module) {
        if (visible == this.visible) return;
        this.visible = visible;
    }

    public String getName() {
        return this.name;
    }

    public String getProfileKey() {
        return this.name;
    }

    public abstract void loadProfile(JsonObject data);
}
