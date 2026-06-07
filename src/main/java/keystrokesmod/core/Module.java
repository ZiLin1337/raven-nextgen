package keystrokesmod.module.core;

import keystrokesmod.event.EventBus;
import keystrokesmod.event.Subscribe;
import keystrokesmod.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;

/**
 * Base Module class for 1.21.4
 */
public abstract class Module {
    private final String name;
    private final Category category;
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();
    
    public enum Category {
        COMBAT, MOVEMENT, RENDER, PLAYER, WORLD, CLIENT, MISC, EXPLOIT
    }
    
    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
        initSettings();
    }
    
    protected abstract void initSettings();
    protected abstract void onEnable();
    protected abstract void onDisable();
    
    public void toggle() {
        if (enabled) disable();
        else enable();
    }
    
    public void enable() {
        if (!enabled) {
            enabled = true;
            onEnable();
            EventBus.getINSTANCE().register(this);
        }
    }
    
    public void disable() {
        if (enabled) {
            enabled = false;
            onDisable();
            EventBus.getINSTANCE().unregister(this);
        }
    }
    
    protected void registerSetting(Setting setting) {
        settings.add(setting);
    }
    
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public List<Setting> getSettings() { return settings; }
}