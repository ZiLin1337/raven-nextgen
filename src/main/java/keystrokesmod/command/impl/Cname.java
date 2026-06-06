package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Cname extends Command {
    public Cname() { super("cname", "clientname"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Current client name: &b" + Raven.clientName);
            return;
        }
        String newName = input.joinArguments(0);
        Raven.clientName = newName;
        reply("&aClient name set to &b" + newName);
    }
}
