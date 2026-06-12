package keystrokesmod.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class StairsUtils {
    
    public static boolean isStairs(Block block) {
        return block instanceof StairsBlock;
    }
    
    public static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof StairsBlock;
    }
    
    public static Direction getStairsFacing(BlockState state) {
        if (state.getBlock() instanceof StairsBlock) {
            return state.get(StairsBlock.FACING);
        }
        return Direction.NORTH;
    }
    
    public static boolean isTopHalf(BlockState state) {
        if (state.getBlock() instanceof StairsBlock) {
            return state.get(StairsBlock.HALF) == net.minecraft.block.enums.BlockHalf.TOP;
        }
        return false;
    }
    
    public static boolean isUpsideDown(BlockState state) {
        return isTopHalf(state);
    }
}
