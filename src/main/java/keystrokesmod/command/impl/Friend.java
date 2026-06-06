package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.utility.Utils;

import java.util.HashSet;
import java.util.Set;

public class Friend extends Command {
    public static Set<String> friends = new HashSet<>();

    public Friend() { super("friend", "f"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() < 1) {
            reply("&7Usage: " + Raven.commandManager.getPrefix() + "friend <add|remove|list> [name]");
            return;
        }
        String action = input.getArgument(0).toLowerCase();
        switch (action) {
            case "add":
                if (input.argumentCount() < 2) { reply("&7Specify a name."); return; }
                friends.add(input.getArgument(1));
                reply("&aAdded &b" + input.getArgument(1) + " &ato friends.");
                break;
            case "remove":
                friends.remove(input.getArgument(1));
                reply("&cRemoved &b" + input.getArgument(1));
                break;
            case "list":
                reply("&bFriends: " + String.join(", ", friends));
                break;
            default:
                reply("&7Unknown action. Use add/remove/list.");
        }
    }

    public static boolean isFriend(String name) { return friends.contains(name); }
}
