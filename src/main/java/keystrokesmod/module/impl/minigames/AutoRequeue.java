package keystrokesmod.module.impl.minigames;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoRequeue extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private SliderSetting delay;
    private String receivedCommand = "";
    private long receiveTime = 0;

    public AutoRequeue() {
        super("Auto Requeue", category.minigames);
        registerSetting(delay = new SliderSetting("Delay", "s", 0.5, 0, 5, 0.1));
    }

    @Override public void onDisable() { receivedCommand = ""; }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || receivedCommand.isEmpty()) return;
        if (System.currentTimeMillis() - receiveTime >= delay.getInput() * 1000) {
            mc.player.networkHandler.sendChatMessage(receivedCommand);
            receivedCommand = "";
        }
    }

    public void onChatMessage(String message) {
        if (!message.contains("play again")) return;
        Pattern p = Pattern.compile("/(\\S+)\\s*");
        Matcher m = p.matcher(message);
        if (m.find()) {
            receivedCommand = "/" + m.group(1);
            receiveTime = System.currentTimeMillis();
        }
    }
}
