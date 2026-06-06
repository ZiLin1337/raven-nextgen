package keystrokesmod.module.impl.combat;
import keystrokesmod.event.SendPacketEvent;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class WTap extends Module {
    private SliderSetting delayBetweenReset;
    private SliderSetting delayUntilReset;
    private SliderSetting chance;
    private ButtonSetting playersOnly;

    private long pendingResetAtMs;
    private long lastResetStartMs;
    private boolean waitingForSprintRestart;
    private boolean wasSprinting;

    public static boolean stopSprint = false;

    public WTap() {
        super("WTap", category.combat);
        this.registerSetting(chance = new SliderSetting("Chance", "%", 100, 0, 100, 1));
        this.registerSetting(delayBetweenReset = new SliderSetting("Delay between reset", "ms", 300, 0, 1000, 10));
        this.registerSetting(delayUntilReset = new SliderSetting("Delay until reset", "ms", 150, 0, 1000, 10));
        this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
    }

    @Override
    public void onEnable() {
        pendingResetAtMs = 0L;
        lastResetStartMs = 0L;
        waitingForSprintRestart = false;
        wasSprinting = false;
        stopSprint = false;
    }

    @Override
    public void onUpdate() {
        if (!Utils.nullCheck() || mc.player == null || mc.player.isDead()) {
            pendingResetAtMs = 0L;
            waitingForSprintRestart = false;
            wasSprinting = false;
            stopSprint = false;
            return;
        }

        long now = System.currentTimeMillis();
        boolean sprintingNow = mc.player.isSprinting();

        if (waitingForSprintRestart && sprintingNow && !wasSprinting) {
            lastResetStartMs = now;
            waitingForSprintRestart = false;
        }

        if (pendingResetAtMs > 0L && now >= pendingResetAtMs) {
            stopSprint = true;
            pendingResetAtMs = 0L;
            waitingForSprintRestart = true;
        }

        wasSprinting = sprintingNow;
    }

    @EventHandler
    public void onSendPacket(SendPacketEvent event) {
        if (!Utils.nullCheck() || mc.player == null || !mc.player.isSprinting()) return;
        if (!(event.getPacket() instanceof PlayerInteractEntityC2SPacket)) return;

        PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();

        // 1.21.4: check if this is an attack packet
        if (packet.getType() != PlayerInteractEntityC2SPacket.InteractType.ATTACK) return;

        if (chance.getInput() == 0) return;

        if (playersOnly.isToggled()) {
            if (mc.world == null || !(mc.world.getEntityById(packet.getId()) instanceof PlayerEntity)) return;
            if (AntiBot.isBot(mc.world.getEntityById(packet.getId()))) return;
        } else {
            if (mc.world == null) return;
            var target = mc.world.getEntityById(packet.getId());
            if (!(target instanceof LivingEntity)) return;
            if (((LivingEntity)target).deathTime != 0) return;
        }

        if (pendingResetAtMs > 0L) return;

        long currentMs = System.currentTimeMillis();
        long betweenResetDelay = (long) delayBetweenReset.getInput();
        if (lastResetStartMs > 0L && currentMs - lastResetStartMs < betweenResetDelay) return;

        if (chance.getInput() != 100.0D) {
            double ch = Math.random();
            if (ch >= chance.getInput() / 100.0D) return;
        }

        pendingResetAtMs = currentMs + (long) delayUntilReset.getInput();
    }

    @Override
    public void onDisable() {
        pendingResetAtMs = 0L;
        lastResetStartMs = 0L;
        waitingForSprintRestart = false;
        wasSprinting = false;
        stopSprint = false;
    }
}
