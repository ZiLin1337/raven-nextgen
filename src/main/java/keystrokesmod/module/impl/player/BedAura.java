package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;

public class BedAura extends Module {
    public BedAura() {
        super("BedAura", category.player);
    }

    public boolean shouldOverrideFastMine() {
        return false;
    }

    public int getBreakDelayTicks() {
        return 0;
    }

    public float getBreakSpeedMultiplier() {
        return 1.0F;
    }

    public net.minecraft.util.math.BlockPos getAuraTargetPos() {
        return null;
    }

    public float getAuraBreakProgress() {
        return 0.0F;
    }
}