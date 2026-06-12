package keystrokesmod.utility;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockSearchIndex {
    private static final Map<String, Block> blockMap = new HashMap<>();
    
    static {
        blockMap.put("chest", Blocks.CHEST);
        blockMap.put("ender_chest", Blocks.ENDER_CHEST);
        blockMap.put("trapped_chest", Blocks.TRAPPED_CHEST);
        blockMap.put("shulker_box", Blocks.SHULKER_BOX);
        blockMap.put("barrel", Blocks.BARREL);
        
        blockMap.put("cobweb", Blocks.COBWEB);
        blockMap.put("web", Blocks.COBWEB);
        blockMap.put("slime", Blocks.SLIME_BLOCK);
        blockMap.put("slime_block", Blocks.SLIME_BLOCK);
        blockMap.put("honey", Blocks.HONEY_BLOCK);
        blockMap.put("honey_block", Blocks.HONEY_BLOCK);
        
        blockMap.put("water", Blocks.WATER);
        blockMap.put("lava", Blocks.LAVA);
        blockMap.put("flowing_water", Blocks.WATER);
        blockMap.put("flowing_lava", Blocks.LAVA);
        
        blockMap.put("obsidian", Blocks.OBSIDIAN);
        blockMap.put("bedrock", Blocks.BEDROCK);
        blockMap.put("end_portal", Blocks.END_PORTAL);
        blockMap.put("barrier", Blocks.BARRIER);
        blockMap.put("air", Blocks.AIR);
    }
    
    public static Block getBlock(String name) {
        return blockMap.get(name.toLowerCase());
    }
}
