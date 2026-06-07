package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.utility.Utils;

public class Debug extends Command {
    public Debug() { super("debug"); }

    @Override
    public void execute(CommandInput input) {
        Raven.DEBUG = !Raven.DEBUG;
        reply("&7Debug mode: " + (Raven.DEBUG ? "&aON" : "&cOFF"));
        if (Raven.DEBUG)) {
            reply("&8Modules: &7" + Raven.moduleManager.getModules().size());
            reply("&8Minecraft: &7" + mc.getGameVersion());
            reply("&8Java: &7" + System.getProperty("java.version"));
        }
    }
}
