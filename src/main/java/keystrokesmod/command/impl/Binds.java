package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Binds extends Command {
    public Binds() {
        super("binds");
    }

    @Override
    public void execute(CommandInput input) {
        reply("&7Bind listing is temporarily unavailable in this migration build.");
    }
}