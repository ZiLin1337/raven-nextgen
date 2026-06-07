package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

public class Toggle extends Command {
    public Toggle() { super("toggle", "t"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "toggle <module>");
            return;
        }
        String moduleName = input.joinArguments(0);
        Module module = Raven.moduleManager.getModule(moduleName);
        if (module == null)) {
            reply("&cModule not found: " + moduleName);
            return;
        }
        module.toggle();
        reply("&7" + module.getName() + " " + (module.isEnabled() ? "&aenabled" : "&cdisabled"));
    }
}
