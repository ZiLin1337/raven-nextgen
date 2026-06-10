package keystrokesmod.utility;

import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Map;

public class SharedBlockHighlightCache {
    private static final Map<BlockPos, Long> blocks = new HashMap<>();

    public static void add(BlockPos pos) {
        blocks.put(pos, System.currentTimeMillis());
    }

    public static boolean contains(BlockPos pos) {
        return blocks.containsKey(pos);
    }

    public static void remove(BlockPos pos) {
        blocks.remove(pos);
    }

    public static void clear() {
        blocks.clear();
    }
}