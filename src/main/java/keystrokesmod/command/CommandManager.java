package keystrokesmod.command;

import keystrokesmod.Raven;
import keystrokesmod.command.impl.*;
import keystrokesmod.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private String prefix = ".";

    public CommandManager() {
        registerCommands();
    }

    private void registerCommands() {
        register(new Bind());
        register(new Binds());
        register(new Cname());
        register(new Debug());
        register(new Enemy());
        register(new Friend());
        register(new Help());
        register(new HideAll());
        register(new Name());
        register(new Ping());
        register(new Prefix());
        register(new Profiles());
        register(new ShowAll());
        register(new Toggle());
        register(new Track());
        register(new Unbind());
    }

    public void register(Command command) {
        commands.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public boolean execute(String message) {
        if (!message.startsWith(prefix)) return false;

        String trimmed = message.substring(prefix.length()).trim();
        if (trimmed.isEmpty()) return false;

        String[] parts = trimmed.split(" ");
        String label = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        Command command = findCommand(label);
        if (command == null) {
            Utils.sendMessage("&7Unknown command. Use &b" + prefix + "help &7for a list of commands.");
            return false;
        }

        CommandInput input = new CommandInput(message, label, args);
        command.execute(input);
        return true;
    }

    private Command findCommand(String label) {
        Command cmd = commands.get(label.toLowerCase());
        if (cmd != null) return cmd;
        for (Command c : commands.values()) {
            if (c.matches(label)) return c;
        }
        return null;
    }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String formatOutput(String message) {
        return "&7[" + Raven.clientName + "&7] &r" + message;
    }

    public List<Command> getCommands() { return new ArrayList<>(commands.values()); }
}
