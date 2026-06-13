package keystrokesmod.script;

import keystrokesmod.module.Module;
import net.minecraft.client.gui.DrawContext;

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
        if (script != null && script.clazz != null) {
            script.invoke("onEnable");
        }
    }
    
    @Override
    public void onDisable() {
        if (script != null && script.clazz != null) {
            script.invoke("onDisable");
        }
    }
    
    @Override
    public void onUpdate() {
        if (script != null && script.clazz != null) {
            script.invoke("onUpdate");
        }
    }
    
    public Script getScript() {
        return script;
    }
}
