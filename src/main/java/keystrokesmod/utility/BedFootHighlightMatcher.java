package keystrokesmod.utility;

import net.minecraft.block.BedBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public final class BedFootHighlightMatcher implements BlockHighlightMatcher {

    @Override
    public boolean matchesBlock(BlockState state) {
        return state != null && state.getBlock() instanceof BedBlock;
    }

    @Override
    public boolean shouldIndexAt(BlockPos pos, BlockState state) {
        if (!matchesBlock(state)) {
            return false;
        }
        return state.getValue((Property) BedBlock.PART) == BedBlockPart.FOOT;
    }
}
