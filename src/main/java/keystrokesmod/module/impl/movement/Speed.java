package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.SafeWalk;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.util.math.BlockPos;

public class Speed extends Module {
    public SliderSetting speed;
    public static SliderSetting multiplier;
    private ButtonSetting onlyForward;
    private ButtonSetting onlyStrafe;

    private static String[] speedOptions = new String[]{"Vanilla", "Float"};

    private boolean canFloat, requireJump;

    public Speed() {
        super("Speed", category.movement);
        this.registerSetting(speed = new SliderSetting("Speed", 0, speedOptions));
        this.registerSetting(multiplier = new SliderSetting("Multiplier", "x", 1.2, 1.0, 1.5, 0.01));
        this.registerSetting(onlyForward = new ButtonSetting("Only forward", false));
        this.registerSetting(onlyStrafe = new ButtonSetting("Only strafe", false));
    }

    public void onPreMotion(PreMotionEvent e) {
        double horizontalSpeed = Utils.getHorizontalSpeed();
        if (horizontalSpeed == 0.0) return;
        if (!mc.player.isOnGround() || mc.player.getAbilities().flying) return;
        if (mc.player.hurtTime == mc.player.maxHurtTime && mc.player.maxHurtTime > 0) return;
        if (Utils.jumpDown()) return;
        if (!settingsMet()) return;

        if (speed.getInput() == 0) {
            double val = multiplier.getInput() - (multiplier.getInput() - 1.0) * 0.5;
            Utils.setSpeed(horizontalSpeed * val, true);
        } else if (speed.getInput() == 1) {
            if (Utils.getHorizontalSpeed() <= 0.1 || floatConditions() {
                canFloat = true;
            }
            if (!floatConditions()) canFloat = false;
            if (!mc.player.isOnGround()) requireJump = false;
            if (canFloat && floatConditions() && !requireJump) {
                e.setPosY(e.getPosY() + 0.0000000000201);
                if (Utils.isMoving() {
                    Utils.setSpeed(getFloatSpeed(getSpeedLevel()));
                }
            }
        }
    }

    public boolean settingsMet() {
        if (onlyForward.isToggled() && !Utils.isBindDown(mc.options.forwardKey)) return false;
        if (onlyStrafe.isToggled() && mc.player.input.movementSideways == 0.0f) return false;
        return true;
    }

    private boolean floatConditions() {
        int edgeY = (int) Math.round((mc.player.getY() % 1.0) * 100.0);
        if (ModuleManager.moduleManager.getModule("BHop").isEnabled() {
            requireJump = true;
            return false;
        }
        if (!(mc.player.getY() % 1 == 0) && edgeY >= 10 && !allowedBlocks() {
            requireJump = true;
            return false;
        }
        if (SafeWalk.canSafeWalk() {
            requireJump = true;
            return false;
        }
        if (!mc.player.isOnGround()) return false;
        if (Utils.jumpDown()) return false;
        if (Utils.isBindDown(mc.options.sneakKey)) return false;
        return true;
    }

    private boolean allowedBlocks() {
        Block block = BlockUtils.getBlockState(BlockPos.ofFloored(mc.player.getX(), mc.player.getY(), mc.player.getZ())).getBlock();
        if (block instanceof SnowBlock) return true;
        if (block instanceof CarpetBlock) return true;
        return false;
    }

    private double[] floatSpeedLevels = {0.2, 0.22, 0.28, 0.29, 0.3};

    private double getFloatSpeed(int speedLevel) {
        double min = 0;
        if (mc.player.input.movementSideways != 0 && mc.player.input.movementForward != 0) min = 0.003;
        if (speedLevel >= 0) return floatSpeedLevels[speedLevel] - min;
        return floatSpeedLevels[0] - min;
    }

    private int getSpeedLevel() {
        if (mc.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED) {
            return mc.player.getStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED).getAmplifier() + 1;
        }
        return 0;
    }
}
