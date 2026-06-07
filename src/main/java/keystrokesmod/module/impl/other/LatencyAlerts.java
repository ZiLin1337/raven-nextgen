package keystrokesmod.module.impl.other;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.UUID;

public class LatencyAlerts extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private SliderSetting threshold;
    private SliderSetting mode;
    private ButtonSetting chatAlert;
    private ButtonSetting soundAlert;
    private String[] modes = {"All players", "Friends only", "Enemies only"};
    private final Map<UUID, Integer> latencyMap = new HashMap<>();
    private long lastAlertTime;

    public LatencyAlerts() {
        super("Latency Alerts", category.other);
        registerSetting(threshold = new SliderSetting("Threshold", 200, 50, 500, 10));
        registerSetting(mode = new SliderSetting("Mode", 0, modes));
        registerSetting(chatAlert = new ButtonSetting("Chat alert", true));
        registerSetting(soundAlert = new ButtonSetting("Sound alert", false));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || mc.player == null || mc.getNetworkHandler() == null) return;
        long now = System.currentTimeMillis();
        if (now - lastAlertTime < 3000) return;

        for (var entry : mc.getNetworkHandler().getPlayerList()) {
            String name = entry.getProfile().getName();
            int ping = entry.getLatency();
            if (ping >= threshold.getInput()) {
                boolean shouldAlert = false;
                int m = (int) mode.getInput();
                if (m == 0) shouldAlert = true;
                else if (m == 1) shouldAlert = Utils.isFriended(name);
                else if (m == 2) shouldAlert = Utils.isEnemy(name);

                if (shouldAlert) {
                    Integer oldPing = latencyMap.get(entry.getProfile().getId());
                    if (oldPing == null || oldPing < threshold.getInput()) {
                        if (chatAlert.isToggled()) {
                            Utils.sendMessage("&7[&cPing&7] &r" + name + " &7has &e" + ping + "ms");
                        }
                        if (soundAlert.isToggled()) {
                            mc.player.playSound(net.minecraft.sound.SoundEvent.of(net.minecraft.util.Identifier.of("note.pling")), 1.0f, 1.0f);
                        }
                        lastAlertTime = now;
                    }
                }
                latencyMap.put(entry.getProfile().getId(), ping);
            }
        }
    }

    @Override
    public void onDisable() { latencyMap.clear(); lastAlertTime = 0; }
}
