package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockAnimationUtils implements IMinecraftInstance {
    private static int swingProgress = 0;
    private static boolean isSwinging = false;
    
    public static void startSwing() {
        isSwinging = true;
        swingProgress = 0;
    }
    
    public static void updateSwing() {
        if (isSwinging) {
            swingProgress++;
            if (swingProgress >= 6) {
                isSwinging = false;
                swingProgress = 0;
            }
        }
    }
    
    public static float getSwingProgress(float partialTicks) {
        if (!isSwinging) return 0;
        float progress = (swingProgress + partialTicks) / 6.0f;
        return Math.min(progress, 1.0f);
    }
    
    public static boolean isSwinging() {
        return isSwinging;
    }
    
    public static void swingArm() {
        if (mc.player != null) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
