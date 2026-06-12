package keystrokesmod.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockListHighlightMatcher {
    private static final Set<Block> highlightedBlocks = new HashSet<>();
    private static final List<BlockPos> highlightedPositions = new ArrayList<>();
    
    public static void addBlock(Block block) {
        highlightedBlocks.add(block);
    }
    
    public static void removeBlock(Block block) {
        highlightedBlocks.remove(block);
    }
    
    public static void addPosition(BlockPos pos) {
        highlightedPositions.add(pos);
    }
    
    public static void removePosition(BlockPos pos) {
        highlightedPositions.remove(pos);
    }
    
    public static void clear() {
        highlightedBlocks.clear();
        highlightedPositions.clear();
    }
    
    public static boolean isHighlighted(World world, BlockPos pos) {
        if (highlightedPositions.contains(pos)) return true;
        BlockState state = world.getBlockState(pos);
        return highlightedBlocks.contains(state.getBlock());
    }
    
    public static Set<Block> getHighlightedBlocks() {
        return highlightedBlocks;
    }
    
    public static List<BlockPos> getHighlightedPositions() {
        return highlightedPositions;
    }
}
