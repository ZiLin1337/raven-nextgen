package keystrokesmod.helper;

import keystrokesmod.Raven;
import keystrokesmod.utility.Utils;
import net.minecraft.client.network.PlayerListEntry;

public class PingHelper {
    public static int getPing() {
        if (Raven.mc.getNetworkHandler() == null || Raven.mc.player == null) return 0;
        PlayerListEntry entry = Raven.mc.getNetworkHandler().getPlayerListEntry(Raven.mc.player.getUuid());
        return entry == null ? 0 : entry.getLatency();
    }

    public static String getPingFormatted() {
        int ping = getPing();
        String color = ping < 50 ? "&a" : ping < 150 ? "&e" : "&c";
        return color + ping + "ms";
    }
}
