package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.utility.Utils;

public class Ping extends Command {
    public Ping() { super("ping", "p"); }

    @Override
    public void execute(CommandInput input) {
        if (mc.getNetworkHandler() == null || mc.player == null) {
            reply("&cNot connected to a server.");
            return;
        }
        int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
        reply("&7Your ping: &b" + ping + "ms");
    }
}
