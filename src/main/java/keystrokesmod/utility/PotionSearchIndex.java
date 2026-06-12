package keystrokesmod.utility;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class PotionSearchIndex {
    private static final Map<String, RegistryEntry<StatusEffect>> potionMap = new HashMap<>();
    
    static {
        potionMap.put("speed", StatusEffects.SPEED);
        potionMap.put("slowness", StatusEffects.SLOWNESS);
        potionMap.put("haste", StatusEffects.HASTE);
        potionMap.put("mining_fatigue", StatusEffects.MINING_FATIGUE);
        potionMap.put("strength", StatusEffects.STRENGTH);
        potionMap.put("weakness", StatusEffects.WEAKNESS);
        potionMap.put("regeneration", StatusEffects.REGENERATION);
        potionMap.put("health_boost", StatusEffects.HEALTH_BOOST);
        potionMap.put("absorption", StatusEffects.ABSORPTION);
        potionMap.put("resistance", StatusEffects.RESISTANCE);
        potionMap.put("fire_resistance", StatusEffects.FIRE_RESISTANCE);
        potionMap.put("invisibility", StatusEffects.INVISIBILITY);
        potionMap.put("night_vision", StatusEffects.NIGHT_VISION);
        potionMap.put("jump_boost", StatusEffects.JUMP_BOOST);
        potionMap.put("nausea", StatusEffects.NAUSEA);
        potionMap.put("regeneration", StatusEffects.REGENERATION);
        potionMap.put("poison", StatusEffects.POISON);
        potionMap.put("wither", StatusEffects.WITHER);
        potionMap.put("water_breathing", StatusEffects.WATER_BREATHING);
        potionMap.put("glowing", StatusEffects.GLOWING);
        potionMap.put("levitation", StatusEffects.LEVITATION);
        potionMap.put("luck", StatusEffects.LUCK);
        potionMap.put("unluck", StatusEffects.UNLUCK);
    }
    
    public static RegistryEntry<StatusEffect> getPotion(String name) {
        return potionMap.get(name.toLowerCase());
    }
    
    public static boolean hasPotion(Iterable<StatusEffectInstance> effects, String name) {
        RegistryEntry<StatusEffect> effect = getPotion(name);
        if (effect == null) return false;
        for (StatusEffectInstance instance : effects) {
            if (instance.getEffectType() == effect) return true;
        }
        return false;
    }
}
