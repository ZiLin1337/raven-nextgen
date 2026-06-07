package keystrokesmod.script;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.TextSetting;

/**
 * Script Manager - disabled in 1.21.4 migration
 * The scripting system uses 1.8.9 APIs that need to be rewritten for 1.21.4
 */
public class Manager extends Module {
    public static TextSetting createScriptName;
    
    public Manager() {
        super("Manager", category.scripts);
        // Script system disabled for 1.21.4 migration
    }
    
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}
