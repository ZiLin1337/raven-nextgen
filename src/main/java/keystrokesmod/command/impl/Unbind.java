package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.module.Module;

public class Unbind extends Command {
    public Unbind() { super("unbind", "ub"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "unbind <module>");
            return;
        }
        Module module = Raven.moduleManager.getModule(input.joinArguments(0));
        if (module == null) {
            reply("&cModule not found.");
            return;
        }
        module.setBind(0);
        reply("&7Unbound &b" + module.getName());
    }
}
