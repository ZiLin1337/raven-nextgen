package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.SendChatEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class ChatCommands extends Module {
    private ButtonSetting publicFeedback;
    private String prefix = ".";

    public ChatCommands() {
        super("ChatCommands", category.client);
        this.registerSetting(publicFeedback = new ButtonSetting("Public feedback", false));
    }

    @EventHandler
    public void onChat(SendChatEvent e) {
        if (mc.player == null) return;
        String msg = e.getMessage();
        if (!msg.startsWith(prefix)) return;
        e.setCanceled(true);
        String cmd = msg.substring(prefix.length()).trim();
        processCommand(cmd);
    }

    private void processCommand(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length == 0) return;
        switch (parts[0].toLowerCase()) {
            case "help": Utils.sendMessage("&7Raven bS [1.21.4] - Commands: .help .toggle .bind"); break;
            case "toggle": if (parts.length >= 2)) { Module m = Raven.moduleManager.getModule(parts[1]); if (m != null) m.toggle(); } break;
            case "bind": Utils.sendMessage("&7Bind via GUI (Right click module) - Coming soon"); break;
            default: Utils.sendMessage("&cUnknown command: " + parts[0]); break;
        }
    }
}
