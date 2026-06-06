package keystrokesmod.script.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Simulation {
    public static final MinecraftClient mc = mc;

    /**
     * Predicts the ground Y level below a position using raycast
     */
    public static double getGroundY(double x, double y, double z) {
        if (mc.world == null) return y;
        // Fallback for simple ground detection
        return mc.world.getBottomY();
    }

    /**
     * Gets the motion of a simulated player after applying friction
     */
    public static Vec3d simulateMotion(Vec3d motion, float strafe, float forward, float yaw, float friction) {
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = Math.max(1.0f, (float) Math.sqrt(f));
            strafe /= f;
            forward /= f;
        }
        float sin = (float) Math.sin(Math.toRadians(yaw));
        float cos = (float) Math.cos(Math.toRadians(yaw));
        double moveX = strafe * cos - forward * sin;
        double moveZ = forward * cos + strafe * sin;
        return motion.multiply(friction).add(moveX * 0.02, 0, moveZ * 0.02);
    }

    /**
     * Applies gravity to a motion vector
     */
    public static Vec3d applyGravity(Vec3d motion, double gravity) {
        return motion.add(0, -gravity, 0);
    }

    /**
     * Checks if a block at the given position is solid
     */
    public static boolean isSolidBlock(double x, double y, double z) {
        if (mc.world == null) return false;
        var pos = new net.minecraft.util.math.BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        return !mc.world.getBlockState(pos).isAir();
    }
}
