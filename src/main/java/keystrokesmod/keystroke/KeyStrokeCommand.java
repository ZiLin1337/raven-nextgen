package keystrokesmod.keystroke;

import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class KeyStrokeCommand extends Command {
    public KeyStrokeCommand() {
        super("keystroke", "ks");
    }

    @Override
    public void execute(CommandInput input) {
        reply("&7Keystroke command is temporarily unavailable in this migration build.");
    }
}