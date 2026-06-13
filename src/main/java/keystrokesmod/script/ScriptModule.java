package keystrokesmod.script;

import keystrokesmod.module.Module;

/**
 * Wrapper class that turns a Script into a Module for the GUI
 */
public class ScriptModule extends Module {
    private final Script script;
    
    public ScriptModule(Script script) {
        super(script.name, Module.category.scripts);
        this.script = script;
    }
    
    @Override
    public void onEnable() {
        // Script enabled
    }
    
    @Override
    public void onDisable() {
        // Script disabled
    }
    
    @Override
    public void onUpdate() {
        // Script update
    }
    
    public Script getScript() {
        return script;
    }
}
