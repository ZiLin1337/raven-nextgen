package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Debug extends Command {
    public Debug() {
        super("debug");
    }

    @Override
    public void execute(CommandInput input) {
        Raven.DEBUG = !Raven.DEBUG;
        reply("&7Debug mode: " + (Raven.DEBUG ? "&aON" : "&cOFF"));
    }
}