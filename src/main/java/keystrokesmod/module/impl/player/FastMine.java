package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;

public class FastMine extends Module {
    public FastMine() {
        super("FastMine", category.player);
    }

    public int getBlockHitDelayOverrideOrMinusOne() {
        return -1;
    }

    public float getBreakSpeedMultiplier() {
        return 1.0F;
    }
}