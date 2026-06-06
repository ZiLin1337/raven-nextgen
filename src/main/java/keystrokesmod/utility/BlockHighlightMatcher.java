package keystrokesmod.utility;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockHighlightMatcher {

    boolean matchesBlock(BlockState state);

    default boolean shouldIndexAt(BlockPos pos, BlockState state) {
        return matchesBlock(state);
    }

    default boolean isActive() {
        return true;
    }

    default void beginScanPass() {
    }
}
