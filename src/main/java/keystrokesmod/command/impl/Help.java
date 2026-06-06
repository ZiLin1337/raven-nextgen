package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.command.CommandInput;
import keystrokesmod.command.CommandManager;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;

public class Help extends Command {
    public Help() { super("help", "?", "commands"); }

    @Override
    public void execute(CommandInput input) {
        if (input.argumentCount() > 0) {
            String query = input.getArgument(0);
            Module module = Raven.moduleManager.getModule(query);
            if (module != null) {
                Utils.sendMessage("&7Module: &b" + module.getName());
                Utils.sendMessage("&7  Keybind: &b" + module.getBind());
                Utils.sendMessage("&7  Category: &b" + module.moduleCategory().name());
                Utils.sendMessage("&7  Enabled: " + (module.isEnabled() ? "&aYes" : "&cNo"));
                return;
            }
        }
        Utils.sendMessage("&bRaven bS &7- Commands (" + Raven.commandManager.getCommands().size() + "):");
        for (Command cmd : Raven.commandManager.getCommands()) {
            Utils.sendMessage("  &b" + Raven.commandManager.getPrefix() + cmd.getName() + " &7- " + getDesc(cmd));
        }
    }

    private String getDesc(Command cmd) {
        if (cmd instanceof Toggle) return "Toggle a module";
        if (cmd instanceof Bind) return "Bind a key to a module";
        if (cmd instanceof Binds) return "List all binds";
        if (cmd instanceof Friend) return "Add/remove friends";
        if (cmd instanceof Enemy) return "Add/remove enemies";
        if (cmd instanceof Ping) return "Check your ping";
        if (cmd instanceof Prefix) return "Change command prefix";
        if (cmd instanceof Help) return "Show this help";
        if (cmd instanceof Profiles) return "Manage profiles";
        if (cmd instanceof Name) return "Show/Hide your name";
        if (cmd instanceof HideAll) return "Hide all modules";
        if (cmd instanceof ShowAll) return "Show all modules";
        if (cmd instanceof Track) return "Track a player";
        if (cmd instanceof Cname) return "Set client name";
        if (cmd instanceof Debug) return "Debug info";
        return "";
    }
}
