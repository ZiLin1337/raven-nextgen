package keystrokesmod.utility;

import keystrokesmod.module.setting.impl.PotionListSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.statuseffect.StatusEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class PotionSearchIndex {
    private static final int MAX = 100;
    private static List<PotionEntry> all;
    private static Map<String, PotionEntry> byKey;

    public static final class PotionEntry {
        public final String key;
        public final String displayName;
        public final StatusEffect effect;
        PotionEntry(String key, String name, StatusEffect effect) {
            this.key = key;
            this.displayName = name;
            this.effect = effect;
        }
    }

    private PotionSearchIndex() {}

    public static List<PotionEntry> search(String query, PotionListSetting setting) {
        ensure();
        String q = (query == null ? "" : query.trim()).toLowerCase(Locale.ROOT);
        return all.stream()
            .filter(e -> !setting.containsPotion(e.key))
            .filter(e -> q.isEmpty() || e.key.toLowerCase(Locale.ROOT).contains(q) || e.displayName.toLowerCase(Locale.ROOT).contains(q))
            .limit(MAX)
            .collect(Collectors.toList());
    }

    private static void ensure() {
        if (all != null) return;
        Map<String, PotionEntry> map = new LinkedHashMap<>();
        for (StatusEffect effect : Registries.STATUS_EFFECT) {
            Identifier id = Registries.STATUS_EFFECT.getId(effect);
            if (id == null) continue;
            String key = id.toString();
            String name = Text.translatable(effect.getTranslationKey()).getString();
            if (name == null || name.isEmpty()) name = key;
            map.put(key, new PotionEntry(key, name, effect));
        }
        all = new ArrayList<>(map.values());
        byKey = map;
    }

    public static ItemStack getItemStack(String key) {
        ensure();
        return new ItemStack(Items.POTION);
    }

    public static String getDisplayName(String key) {
        ensure();
        PotionEntry e = byKey != null ? byKey.get(key) : null;
        return e != null ? e.displayName : key;
    }
}
