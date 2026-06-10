package keystrokesmod.utility;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RotationUtils implements IMinecraftInstance {
    public static float[] getRotations(Entity entity) {
        return new float[]{0, 0};
    }

    public static float[] getRotations(BlockPos pos) {
        return new float[]{0, 0};
    }

    public static float[] getRotationsTo(double x, double y, double z) {
        return new float[]{0, 0};
    }

    public static float wrapAngleTo180(float angle) {
        return MathHelper.wrapDegrees(angle);
    }
}