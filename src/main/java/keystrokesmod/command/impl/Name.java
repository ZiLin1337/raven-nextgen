package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Name extends Command {
    public Name() { super("name", "nick"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Current name: &b" + mc.getSession().getUsername());
            return;
        }
        String newName = input.joinArguments(0);
        reply("&7Name display changed to &b" + newName);
    }
}
