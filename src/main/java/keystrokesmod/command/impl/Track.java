package keystrokesmod.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

public class Track extends Command {
    private static final Map<String, List<double[]>> trackedPlayers = new HashMap<>();

    public Track() {
        super("track", "t");
    }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: .track <add|remove|clear|list> [name]");
            return;
        }

        String action = input.getArgument(0).toLowerCase();
        switch (action) {
            case "add":
                if (input.argumentCount() < 2) {
                    reply("&7Usage: .track add <name>");
                    return;
                }
                String playerName = input.getArgument(1);
                trackedPlayers.put(playerName, new ArrayList<>());
                reply("&aStarted tracking &b" + playerName);
                break;
            case "remove":
                if (input.argumentCount() < 2) {
                    reply("&7Usage: .track remove <name>");
                    return;
                }
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
    }

    public static List<double[]> getTrackData(String name) {
        return trackedPlayers.getOrDefault(name, new ArrayList<>());
    }
}