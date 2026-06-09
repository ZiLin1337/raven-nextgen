package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Help extends Command {
    public Help() {
        super("help", "h");
    }

    @Override
    public void execute(CommandInput input) {
        reply("&7Help output is temporarily simplified in this migration build.");
    }
}