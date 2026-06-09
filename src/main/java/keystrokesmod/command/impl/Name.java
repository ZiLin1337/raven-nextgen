package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Name extends Command {
    public Name() {
        super("name", "nick");
    }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Name display command is temporarily limited in this migration build.");
            return;
        }
        reply("&7Name display changed to &b" + input.joinArguments(0));
    }
}