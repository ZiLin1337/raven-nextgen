package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;

public class ShowAll extends Command {
    public ShowAll() { super("showall", "show"); }

    @Override
    public void execute(CommandInput input) {
        int count = 0;
        for (Module m : Raven.moduleManager.getModules() {
            if (!m.isEnabled() { m.enable(); count++; }
        }
        reply("&aEnabled &b" + count + " &7modules.");
    }
}
