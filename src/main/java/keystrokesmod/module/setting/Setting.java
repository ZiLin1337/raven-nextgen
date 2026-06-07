package keystrokesmod.module.setting;

import com.google.gson.JsonObject;

/**
 * Base Setting class for 1.21.4
 */
public abstract class Setting {
    private final String name;
    private final String description;
    private boolean visible = true;
    
    public Setting(String name) {
        this(name, "");
    }
    
    public Setting(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public abstract void loadProfile(JsonObject data);
    public abstract void saveProfile(JsonObject data);
}