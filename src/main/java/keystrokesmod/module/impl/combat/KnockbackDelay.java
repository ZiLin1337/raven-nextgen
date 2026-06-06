package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ItemListSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.CombatTargeting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class KnockbackDelay extends Module {
    private final SliderSetting distanceToTarget;
    private final SliderSetting chance;
    private final SliderSetting maximumDelay;
    private final ButtonSetting inAir;
    private final ButtonSetting lookingAtPlayer;
    private final ButtonSetting requireLeftMouse;
    private final ButtonSetting onlyWhitelistedItem;
    private final ItemListSetting whitelistedItems;

    private boolean hasPendingVelocity;
    private long velocityReceivedAt;
    private EntityVelocityUpdateS2CPacket pendingVelocity;

    public KnockbackDelay() {
        super("Knockback Delay", category.combat);
        this.registerSetting(distanceToTarget = new SliderSetting("Distance to target", 6.0, 3.0, 12.0, 0.1));
        this.registerSetting(chance = new SliderSetting("Chance", "%", 100.0, 0.0, 100.0, 1.0));
        this.registerSetting(maximumDelay = new SliderSetting("Maximum delay", "ms", 200.0, 50.0, 1000.0, 10.0));
        this.registerSetting(new DescriptionSetting("Conditions"));
        this.registerSetting(inAir = new ButtonSetting("In air", true));
        this.registerSetting(lookingAtPlayer = new ButtonSetting("Looking at player", false));
        this.registerSetting(requireLeftMouse = new ButtonSetting("Require left mouse", false));
        this.registerSetting(onlyWhitelistedItem = new ButtonSetting("Restrict held item", false));
        this.registerSetting(whitelistedItems = new ItemListSetting("Whitelisted items"));
    }

    @Override
    public void onEnable() {
        hasPendingVelocity = false;
        pendingVelocity = null;
    }

    @Override
    public void onDisable() {
        flushPending();
    }

    @Override
    public String getInfo() {
        return (int) maximumDelay.getInput() + "ms";
    }

    @Override
    public void guiUpdate() {
        whitelistedItems.setVisible(onlyWhitelistedItem.isToggled(), this);
    }

    @EventHandler
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!isEnabled() || e.isCanceled()) return;

        // Position sync cancels delay
        if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            flushPending();
            return;
        }

        if (!(e.getPacket() instanceof EntityVelocityUpdateS2CPacket)) return;
        if (!Utils.nullCheck() || mc.player == null || mc.world == null) return;

        EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) e.getPacket();
        if (packet.getId() != mc.player.getId()) return;

        String failReason = conditionsFailureReason();
        if (failReason != null) return;

        if (chance.getInput() < 100.0 && Math.random() * 100.0 >= chance.getInput()) return;

        if (hasPendingVelocity) return;

        // Cancel the original packet and store it for delayed processing
        e.setCanceled(true);
        hasPendingVelocity = true;
        pendingVelocity = packet;
        velocityReceivedAt = System.currentTimeMillis();
    }

    @Override
    public void onUpdate() {
        if (!isEnabled() || !hasPendingVelocity || pendingVelocity == null) return;

        long now = System.currentTimeMillis();
        long elapsed = now - velocityReceivedAt;

        if (elapsed >= maximumDelay.getInput()) {
            // Re-apply the velocity
            if (mc.player != null) {
                mc.player.setVelocity(
                    pendingVelocity.getVelocityX() / 8000.0,
                    pendingVelocity.getVelocityY() / 8000.0,
                    pendingVelocity.getVelocityZ() / 8000.0
                );
            }
            hasPendingVelocity = false;
            pendingVelocity = null;
            return;
        }

        // Check if conditions are still failing, flush if so
        if (conditionsFailureReason() != null) {
            flushPending();
            return;
        }
    }

    private String conditionsFailureReason() {
        double maxSq = distanceToTarget.getInput() * distanceToTarget.getInput();
        if (CombatTargeting.findTarget(maxSq, false) == null) {
            return "no target in range";
        }

        if (inAir.isToggled() && mc.player != null && mc.player.isOnGround()) {
            return "not in air";
        }

        if (lookingAtPlayer.isToggled() && CombatTargeting.getMouseOverTarget(maxSq) == null) {
            return "not looking at player";
        }

        if (requireLeftMouse.isToggled() && !mc.mouse.isLeftButtonPressed()) {
            return "LMB not held";
        }

        if (onlyWhitelistedItem.isToggled() && mc.player != null) {
            ItemStack held = mc.player.getMainHandStack();
            if (held.isEmpty() || !whitelistedItems.matches(held)) {
                return "held item not whitelisted";
            }
        }

        return null;
    }

    private void flushPending() {
        hasPendingVelocity = false;
        pendingVelocity = null;
    }
}
