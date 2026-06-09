package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Ping extends Command {
    public Ping() {
        super("ping", "p");
    }

    @Override
    public void execute(CommandInput input) {
        reply("&7Ping command is temporarily unavailable in this migration build.");
    }
}