package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class AntiAFK extends Module {
    private SliderSetting afk;
    private ButtonSetting jump;
    private ButtonSetting jumpWhenCollided;
    private ButtonSetting randomClicks;
    private ButtonSetting swapItem;
    private SliderSetting spin;
    private ButtonSetting randomizeDelta;
    private ButtonSetting randomizePitch;
    private SliderSetting minDelay;
    private SliderSetting maxDelay;

    private String[] afkModes = {"None", "Wander", "Lateral shuffle", "Forward", "Backward"};
    private String[] spinModes = {"None", "Random", "Right", "Left"};

    private int ticks;
    private boolean random;
    public boolean stop = false;

    public AntiAFK() {
        super("AntiAFK", category.player);
        this.registerSetting(afk = new SliderSetting("AFK", 0, afkModes));
        this.registerSetting(jump = new ButtonSetting("Jump", false));
        this.registerSetting(jumpWhenCollided = new ButtonSetting("Jump only when collided", false));
        this.registerSetting(randomClicks = new ButtonSetting("Random clicks", false));
        this.registerSetting(swapItem = new ButtonSetting("Swap item", false));
        this.registerSetting(spin = new SliderSetting("Spin", 0, spinModes));
        this.registerSetting(randomizeDelta = new ButtonSetting("Randomize delta", true));
        this.registerSetting(randomizePitch = new ButtonSetting("Randomize pitch", true));
        this.registerSetting(minDelay = new SliderSetting("Minimum delay ticks", 10.0, 4.0, 160.0, 2.0));
        this.registerSetting(maxDelay = new SliderSetting("Maximum delay ticks", 80.0, 4.0, 160.0, 2.0));
    }

    public void onEnable() {
        this.ticks = h();
        this.random = Utils.getRandom().nextBoolean();
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (stop) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) return;
        if (mc.player == null) return;

        --this.ticks;
        switch ((int) afk.getInput()) {
            case 1: {
                if (this.random) {
                    mc.options.forwardKey.setPressed(Utils.getRandom().nextBoolean());
                    mc.options.rightKey.setPressed(Utils.getRandom().nextBoolean());
                } else {
                    mc.options.backKey.setPressed(Utils.getRandom().nextBoolean());
                    mc.options.leftKey.setPressed(Utils.getRandom().nextBoolean());
                }
                break;
            }
            case 2: {
                mc.options.rightKey.setPressed(this.random);
                mc.options.leftKey.setPressed(!this.random);
                break;
            }
            case 3: {
                mc.options.forwardKey.setPressed(true);
                break;
            }
            case 4: {
                mc.options.backKey.setPressed(true);
                break;
            }
        }

        switch ((int) spin.getInput()) {
            case 1: {
                mc.player.setYaw(mc.player.getYaw() + c(this.random));
                d();
                break;
            }
            case 2: {
                mc.player.setYaw(mc.player.getYaw() + c(true));
                d();
                break;
            }
            case 3: {
                mc.player.setYaw(mc.player.getYaw() + c(false));
                d();
                break;
            }
        }

        if (jump.isToggled() && mc.player.isOnGround() && (!jumpWhenCollided.isToggled() || mc.horizontalCollision)) {
            mc.player.jump();
        }

        if (this.ticks == 0) {
            if (swapItem.isToggled()) {
                mc.player.getInventory().selectedSlot = Utils.randomizeInt(0, 8);
            }
            if (randomClicks.isToggled()) {
                mc.doAttack();
            }
            this.ticks = h();
            this.random = !this.random;
        }
    }

    public void onDisable() {
        b(0);
        stop = false;
    }

    private void b(int n) {
        switch (n) {
            case 1: {
                mc.options.forwardKey.setPressed(false);
                mc.options.backKey.setPressed(false);
                mc.options.rightKey.setPressed(false);
                mc.options.leftKey.setPressed(false);
                break;
            }
            case 2: {
                mc.options.rightKey.setPressed(false);
                mc.options.leftKey.setPressed(false);
                break;
            }
            case 3: {
                mc.options.forwardKey.setPressed(false);
                break;
            }
            case 4: {
                mc.options.backKey.setPressed(false);
                break;
            }
        }
    }

    private int h() {
        if (minDelay.getInput() == maxDelay.getInput()) return (int) minDelay.getInput();
        return Utils.randomizeInt((int) minDelay.getInput(), (int) maxDelay.getInput());
    }

    private void d() {
        if (randomizePitch.isToggled()) {
            mc.player.setPitch(RotationUtils.clampPitch((float) (mc.player.getPitch() + a());
        }
    }

    private double c(boolean b) {
        int n = b ? 1 : -1;
        if (!randomizeDelta.isToggled()) return 3 * n;
        double n2 = Utils.randomizeInt(100, 1000) / 100.0;
        if (n2 % 1.0 == 0.0) n2 += Utils.randomizeInt(1, 10) / 10.0 * n;
        return n2 * n;
    }

    private double a() {
        int n = Utils.getRandom().nextBoolean() ? 1 : -1;
        if (!randomizeDelta.isToggled()) return 2 * n;
        double n2 = Utils.randomizeInt(100, 500) / 100.0;
        if (n2 % 1.0 == 0.0) n2 += Utils.randomizeInt(1, 10) / 10.0 * n;
        return n2 * n;
    }
}