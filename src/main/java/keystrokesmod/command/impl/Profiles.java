package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Profiles extends Command {
    public Profiles() { super("profiles", "profile", "pfl"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "profiles <save|load|list|delete> [name]");
            reply("&8Profile saving/loading coming soon - configs are auto-saved.");
            return;
        }
        reply("&7Profile system coming soon.");
    }
}
