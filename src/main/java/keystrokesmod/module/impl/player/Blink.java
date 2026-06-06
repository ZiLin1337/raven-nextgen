package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {
    private static final String[] MODE_LABELS = {"Inbound", "Outbound", "Both"};
    private SliderSetting mode;
    private SliderSetting disableAfterMs;
    private ButtonSetting maxDuration;
    private ButtonSetting disableOnAttack;
    private ButtonSetting initialPosition;
    private Vec3d pos;
    private int blinkTicks;
    private long enableTime;
    private final List<Packet<?>> queuedPackets = new ArrayList<>();

    public Blink() {
        super("Blink", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", 1, MODE_LABELS));
        this.registerSetting(maxDuration = new ButtonSetting("Max duration", false));
        this.registerSetting(disableAfterMs = new SliderSetting("Disable after", "ms", 500.0, 50.0, 20000.0, 50.0));
        this.registerSetting(new DescriptionSetting("Disable on"));
        this.registerSetting(disableOnAttack = new ButtonSetting("Attack", false));
        this.registerSetting(initialPosition = new ButtonSetting("Show initial position", true));
    }

    @Override
    public void guiUpdate() { disableAfterMs.setVisible(maxDuration.isToggled(), this); }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        pos = mc.player.getPos();
        blinkTicks = 0;
        enableTime = System.currentTimeMillis();
        queuedPackets.clear();
    }

    public boolean delaysInboundPackets() { int m = (int) mode.getInput(); return m == 0 || m == 2; }
    public boolean delaysOutboundPackets() { int m = (int) mode.getInput(); return m == 1 || m == 2; }
    @Override public String getInfo() { return String.valueOf(blinkTicks); }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        ++blinkTicks;
        if (maxDuration.isToggled()) {
            long elapsed = System.currentTimeMillis() - enableTime;
            if (elapsed >= (int) disableAfterMs.getInput()) this.disable();
        }
    }

    @EventHandlerpublic void onSendPacket(SendPacketEvent event) {
        if (!isEnabled() || !Utils.nullCheck()) return;
        Packet<?> packet = event.getPacket();
        if (delaysOutboundPackets() && packet instanceof PlayerMoveC2SPacket) {
            event.setCanceled(true);
            queuedPackets.add(packet);
            return;
        }
        if (disableOnAttack.isToggled() && packet instanceof net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        for (Packet<?> packet : queuedPackets) {
            if (mc.getNetworkHandler() != null) mc.getNetworkHandler().sendPacket(packet);
        }
        queuedPackets.clear();
        pos = null;
        blinkTicks = 0;
    }

    public int getQueuedPackets() { return queuedPackets.size(); }
}
