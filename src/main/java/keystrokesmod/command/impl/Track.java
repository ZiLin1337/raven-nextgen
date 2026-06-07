package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.LivingEntity;

import java.util.*;

public class Track extends Command {
    private static final Map<String, List<double[]>> trackedPlayers = new HashMap<>();

    public Track() { super("track", "t"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 2) {
            reply("&7Usage: .track <add|remove|clear|list> [name]");
            return;
        }
        String action = input.getArgument(0).toLowerCase();
        switch (action) {
            case "add":
                String playerName = input.getArgument(1);
                trackedPlayers.put(playerName, new ArrayList<>());
                reply("&aStarted tracking &b" + playerName);
                break;
            case "remove":
                String name = input.getArgument(1);
                trackedPlayers.remove(name);
                reply("&cStopped tracking &b" + name);
                break;
            case "clear":
                trackedPlayers.clear();
                reply("&cCleared all tracking data");
                break;
            case "list":
                reply("&bTracked players: " + String.join(", ", trackedPlayers.keySet()));
                break;
            default:
                reply("&7Unknown action: " + action);
        }
    }

    public static void update() {
        for (Map.Entry<String, List<double[]>> entry : trackedPlayers.entrySet()) {
            if (mc.world == null) continue;
            String name = entry.getKey();
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p.getName().getString().equalsIgnoreCase(name)) {
                    entry.getValue().add(new double[]) {p.getX(), p.getY(), p.getZ()});
                    break;
                }
            }
        }
    }

    public static List<double[]> getTrackData(String name) {
        return trackedPlayers.getOrDefault(name, new ArrayList<>());
    }
}