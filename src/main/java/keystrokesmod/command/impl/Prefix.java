package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Prefix extends Command {
    public Prefix() { super("prefix"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Current prefix: &b" + Raven.commandManager.getPrefix());
            return;
        }
        String newPrefix = input.getArgument(0);
        if (newPrefix.length() > 5) {
            reply("&cPrefix too long (max 5 chars)");
            return;
        }
        Raven.commandManager.setPrefix(newPrefix);
        reply("&aPrefix set to &b" + newPrefix);
    }
}
