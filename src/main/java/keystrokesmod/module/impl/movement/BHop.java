package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.GroupSetting;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.ModuleUtils;
import keystrokesmod.utility.Utils;

public class BHop extends Module {
    public SliderSetting mode;
    public static SliderSetting speedSetting;
    private ButtonSetting liquidDisable, sneakDisable, jumpMoving;
    public ButtonSetting rotateYawOption, damageBoost, damageBoostRequireKey;
    public GroupSetting damageBoostGroup;
    public KeySetting damageBoostKey;
    public String[] modes = new String[]{"Strafe", "Ground", "9 tick", "8 tick", "7 tick"};
    public boolean hopping, lowhop, didMove, setRotation;

    public BHop() {
        super("BHop", Module.category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", 0, modes));
        this.registerSetting(speedSetting = new SliderSetting("Speed", 2.0, 0.8, 1.2, 0.01));
        this.registerSetting(liquidDisable = new ButtonSetting("Disable in liquid", true));
        this.registerSetting(sneakDisable = new ButtonSetting("Disable while sneaking", true));
        this.registerSetting(jumpMoving = new ButtonSetting("Only jump when moving", true));
        this.registerSetting(rotateYawOption = new ButtonSetting("Rotate yaw", false));
        this.registerSetting(damageBoostGroup = new GroupSetting("Damage boost"));
        this.registerSetting(damageBoost = new ButtonSetting(damageBoostGroup, "Enable", false));
        this.registerSetting(damageBoostRequireKey = new ButtonSetting(damageBoostGroup, "Require key", false));
        this.registerSetting(damageBoostKey = new KeySetting(damageBoostGroup, "Enable key", 51));
    }

    public void guiUpdate() {
        this.damageBoostKey.setVisible(damageBoostRequireKey.isToggled(), this);
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    public void onPostPlayerInput(PostPlayerInputEvent e) {
        if (!mc.player.isOnGround() || mc.player.getAbilities().flying) return;
        if (hopping) {
            mc.options.jumpKey.setPressed(false);
        }
    }

    public void onPreMotion(PreMotionEvent e) {
        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && liquidDisable.isToggled()) return;
        if (mc.player.isSneaking() && sneakDisable.isToggled()) return;
        if (ModuleManager.moduleManager.getModule("LongJump").isEnabled()) return;

        if (mode.getInput() >= 1) {
            if (mc.player.isOnGround() && (!jumpMoving.isToggled() || Utils.isMoving())) {
                if (mc.player.input.movementForward <= -0.5 && mc.player.input.movementSideways == 0
                        && KillAura.target == null && !mc.player.horizontalCollision) {
                    setRotation = true;
                }
                mc.player.jump();
                double speed = (speedSetting.getInput() - 0.52);
                int speedAmplifier = Utils.getSpeedAmplifier();
                double speedModifier = switch (speedAmplifier) {
                    case 1 -> speed + 0.02;
                    case 2 -> speed + 0.04;
                    case 3 -> speed + 0.1;
                    default -> speed;
                };
                if (Utils.isMoving()) {
                    double randOffset = 0.0001 + Math.random() * 0.0002;
                    Utils.setSpeed(speedModifier - randOffset, true);
                    didMove = true;
                }
                hopping = true;
            }
            if (mc.player.input.movementForward <= 0.5 && hopping) {
                ModuleUtils.handleSlow();
            }
            if (!mc.player.isOnGround()) hopping = false;
        }
        switch ((int) mode.getInput() {
            case 0:
                if (Utils.isMoving()) {
                    if (mc.player.isOnGround()) mc.player.jump();
                    mc.player.setSprinting(true);
                    Utils.setSpeed(Utils.getHorizontalSpeed() + 0.005 * speedSetting.getInput(), true);
                    hopping = true;
                }
                break;
        }
    }

    public void onDisable() {
        hopping = false;
    }
}
