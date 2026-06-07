package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

public class Bind extends Command {
    public Bind() { super("bind", "b"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 2) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "bind <module> <key>");
            return;
        }
        String moduleName = input.getArgument(0);
        String keyName = input.getArgument(1).toUpperCase();
        Module module = Raven.moduleManager.getModule(moduleName);
        if (module == null)) {
            reply("&cModule not found: " + moduleName);
            return;
        }
        int keyCode = getKeyCode(keyName);
        if (keyCode == -1)) {
            reply("&cUnknown key: " + keyName);
            return;
        }
        module.setBind(keyCode);
        reply("&7Bound &b" + module.getName() + " &7to &b" + keyName);
    }

    private int getKeyCode(String name) {
        try {
            return org.lwjgl.glfw.GLFW.class.getField("GLFW_KEY_" + name).getInt(null);
        } catch (Exception e) {
            return -1;
        }
    }
}
