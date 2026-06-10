package keystrokesmod.utility;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class BlockUtils implements IMinecraftInstance {
    public static boolean isSamePos(BlockPos pos1, BlockPos pos2) {
        return pos1.equals(pos2);
    }

    public static BlockPos pos(double x, double y, double z) {
        return BlockPos.ofFloored(x, y, z);
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (mc.world == null) return null;
        return mc.world.getBlockState(pos);
    }

    public static float getBlockHardness(BlockState state) {
        return state.getHardness(mc.world, pos(0, 0, 0));
    }
}
