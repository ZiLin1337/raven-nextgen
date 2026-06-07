package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;

public class HideAll extends Command {
    public HideAll() { super("hideall", "hide"); }

    @Override
    public void execute(CommandInput input) {
        int count = 0;
        for (Module m : Raven.moduleManager.getModules()) {
            if (m.isEnabled()) { m.disable(); count++; }
        }
        reply("&7Disabled &b" + count + " &7modules.");
    }
}
