package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.JumpEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InvMove extends Module {
    public SliderSetting inventory;
    private SliderSetting chestAndOthers;
    private SliderSetting motion;
    private ButtonSetting modifyMotionPost;
    private ButtonSetting slowWhenNecessary;
    private ButtonSetting allowJumping;
    private ButtonSetting allowSprinting;
    private ButtonSetting allowRotating;

    public int ticks;
    public boolean setMotion;

    private final String[] INVENTORY_MODES = new String[]{"Disabled", "Vanilla", "Blink", "Close"};
    private final String[] CHEST_AND_OTHER_MODES = new String[]{"Disabled", "Vanilla", "Blink"};

    public InvMove() {
        super("InvMove", Module.category.movement);
        this.registerSetting(inventory = new SliderSetting("Inventory", 1, INVENTORY_MODES));
        this.registerSetting(chestAndOthers = new SliderSetting("Chest & others", 1, CHEST_AND_OTHER_MODES));
        this.registerSetting(motion = new SliderSetting("Motion", "x", 1, 0.05, 1, 0.01));
        this.registerSetting(modifyMotionPost = new ButtonSetting("Modify motion after click", false));
        this.registerSetting(slowWhenNecessary = new ButtonSetting("Slow motion when necessary", false));
        this.registerSetting(allowJumping = new ButtonSetting("Allow jumping", true));
        this.registerSetting(allowRotating = new ButtonSetting("Allow rotating", true));
        this.registerSetting(allowSprinting = new ButtonSetting("Allow sprinting", true));
    }

    @Override
    public void onEnable() {
        Raven.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        reset();
        Raven.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.player == null) return;
        if (!guiCheck()) {
            reset();
            return;
        }

        if (setMotion) {
            if (++ticks == 10) {
                ticks = 0;
                setMotion = false;
            }
            if (motion.getInput() != 1 || slowWhenNecessary.isToggled()) {
                int speedAmplifier = 0;
                if (mc.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED)) {
                    speedAmplifier = mc.player.getStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED).getAmplifier() + 1;
                }
                double slowedMotion = 0.65;
                switch (speedAmplifier) {
                    case 1: slowedMotion = 0.615; break;
                    case 2: slowedMotion = 0.3; break;
                }
                Utils.setSpeed(Utils.getHorizontalSpeed() * (slowWhenNecessary.isToggled() ? slowedMotion : motion.getInput()));
            }
        }

        mc.options.forwardKey.setPressed(mc.options.forwardKey.isPressed());
        mc.options.backKey.setPressed(mc.options.backKey.isPressed());
        mc.options.rightKey.setPressed(mc.options.rightKey.isPressed());
        mc.options.leftKey.setPressed(mc.options.leftKey.isPressed());
        mc.options.jumpKey.setPressed(Utils.jumpDown());
        mc.options.sprintKey.setPressed(mc.options.sprintKey.isPressed());

        boolean foodLvlMet = mc.player.getHungerManager().getFoodLevel() > 6 || mc.player.getAbilities().allowFlying;
        if ((mc.options.sprintKey.isPressed() || (ModuleManager.sprint != null && ModuleManager.sprint.isEnabled()))
                && mc.player.input.movementForward >= 0.8F && foodLvlMet && !mc.player.isSprinting()
                && allowSprinting.isToggled()) {
            mc.player.setSprinting(true);
        }
        if (!allowSprinting.isToggled()) {
            mc.player.setSprinting(false);
        }
        if (allowRotating.isToggled()) {
            float pitch = mc.player.getPitch();
            float yaw = mc.player.getYaw();
            if (isKeyDown(264) && pitch < 90.0F) mc.player.setPitch(pitch + 6.0F);
            if (isKeyDown(265) && pitch > -90.0F) mc.player.setPitch(pitch - 6.0F);
            if (isKeyDown(262)) mc.player.setYaw(yaw + 6.0F);
            if (isKeyDown(263)) mc.player.setYaw(yaw - 6.0F);
        }
    }

    @EventHandler
    public void onJump(JumpEvent e) {
        if (!allowJumping.isToggled() && mc.currentScreen != null) {
            e.setCancelled(true);
        }
    }

    private void reset() {
        ticks = 0;
        setMotion = false;
    }

    private boolean guiCheck() {
        if (mc.currentScreen == null) return false;
        if (Settings.inInventory()) {
            if (inventory.getInput() == 0) return false;
        } else if (!Settings.inInventory() && chestAndOthers.getInput() == 0 && !isClickGui()) {
            return false;
        }
        return true;
    }

    private boolean isClickGui() {
        return false;
    }

    private boolean isKeyDown(int keyCode) {
        long handle = mc.getWindow().getHandle();
        return org.lwjgl.glfw.GLFW.glfwGetKey(handle, keyCode) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
    }
}