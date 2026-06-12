package keystrokesmod.utility;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ItemSearchIndex {
    private static final Map<String, Item> itemMap = new HashMap<>();
    
    static {
        itemMap.put("sword", Items.DIAMOND_SWORD);
        itemMap.put("diamond_sword", Items.DIAMOND_SWORD);
        itemMap.put("iron_sword", Items.IRON_SWORD);
        itemMap.put("stone_sword", Items.STONE_SWORD);
        itemMap.put("wooden_sword", Items.WOODEN_SWORD);
        itemMap.put("golden_sword", Items.GOLDEN_SWORD);
        itemMap.put("netherite_sword", Items.NETHERITE_SWORD);
        
        itemMap.put("pickaxe", Items.DIAMOND_PICKAXE);
        itemMap.put("diamond_pickaxe", Items.DIAMOND_PICKAXE);
        itemMap.put("iron_pickaxe", Items.IRON_PICKAXE);
        itemMap.put("stone_pickaxe", Items.STONE_PICKAXE);
        itemMap.put("wooden_pickaxe", Items.WOODEN_PICKAXE);
        
        itemMap.put("axe", Items.DIAMOND_AXE);
        itemMap.put("diamond_axe", Items.DIAMOND_AXE);
        itemMap.put("iron_axe", Items.IRON_AXE);
        
        itemMap.put("shovel", Items.DIAMOND_SHOVEL);
        itemMap.put("bow", Items.BOW);
        itemMap.put("crossbow", Items.CROSSBOW);
        itemMap.put("trident", Items.TRIDENT);
        itemMap.put("shield", Items.SHIELD);
        itemMap.put("ender_pearl", Items.ENDER_PEARL);
        itemMap.put("ender_pearls", Items.ENDER_PEARL);
        itemMap.put("apple", Items.GOLDEN_APPLE);
        itemMap.put("gap", Items.GOLDEN_APPLE);
        itemMap.put("gapple", Items.GOLDEN_APPLE);
        itemMap.put("enchant_golden_apple", Items.ENCHANTED_GOLDEN_APPLE);
        itemMap.put("totem", Items.TOTEM_OF_UNDYING);
        itemMap.put("crystal", Items.END_CRYSTAL);
    }
    
    public static Item getItem(String name) {
        return itemMap.get(name.toLowerCase());
    }
}
