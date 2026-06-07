package keystrokesmod.keystroke;

import keystrokesmod.Raven;
import keystrokesmod.command.CommandInput;
import keystrokesmod.command.impl.Toggle;
import net.minecraft.client.MinecraftClient;

public class KeyStrokeCommand {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void execute(String cmd) {
        if (cmd.startsWith("keystroke")) {
            String[] parts = cmd.split(" ");
            if (parts.length > 1 && parts[1].equals("gui")) {
                mc.setScreen(new KeyStrokeConfigGui());
            }
        } else if (cmd.startsWith("toggle ")) {
            String moduleName = cmd.substring(7);
            Raven.moduleManager.getModule(moduleName).toggle();
        }
    }
}
