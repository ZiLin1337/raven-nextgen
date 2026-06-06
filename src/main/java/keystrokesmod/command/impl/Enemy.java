package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;

import java.util.HashSet;
import java.util.Set;

public class Enemy extends Command {
    public static Set<String> enemies = new HashSet<>();

    public Enemy() { super("enemy", "e"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "enemy <add|remove|list> [name]");
            return;
        }
        String action = input.getArgument(0).toLowerCase();
        switch (action) {
            case "add":
                enemies.add(input.getArgument(1));
                reply("&cAdded &b" + input.getArgument(1) + " &cto enemies.");
                break;
            case "remove":
                enemies.remove(input.getArgument(1));
                reply("&aRemoved &b" + input.getArgument(1));
                break;
            case "list":
                reply("&cEnemies: " + String.join(", ", enemies));
                break;
            default:
                reply("&7Unknown action.");
        }
    }

    public static boolean isEnemy(String name) { return enemies.contains(name); }
}
