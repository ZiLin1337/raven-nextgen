package keystrokesmod.script;

import keystrokesmod.module.Module;

public class Script extends Module {
    public boolean error = false;
    public String scriptName;
    public String name;

    public Script(String name) {
        super(name, Module.category.client);
        this.scriptName = name;
        this.name = name;
    }
}
