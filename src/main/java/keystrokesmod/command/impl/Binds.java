package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

public class Binds extends Command {
    public Binds() { super("binds"); }

    @Override
    public void execute(CommandInput input) {
        Utils.sendMessage("&bCurrent binds:");
        for (Module module : Raven.moduleManager.getModules()) {
            if (module.getBind() != 0) {
                String keyName = getKeyName(module.getBind());
                Utils.sendMessage("  &b" + module.getName() + " &7-> &b" + keyName);
            }
        }
    }

    private String getKeyName(int key) {
        try {
            java.lang.reflect.Field[] fields = org.lwjgl.glfw.GLFW.class.getFields();
            for (java.lang.reflect.Field f : fields) {
                if (f.getName().startsWith("GLFW_KEY_") && f.getInt(null) == key) {
                    return f.getName().substring(9);
                }
            }
        } catch (Exception e) {}
        return "NONE";
    }
}
