package keystrokesmod.utility;

import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

public final class ItemSortScoring {
    private ItemSortScoring() {
    }

    public static double getMeleeDamage(ItemStack stack) {
        if (stack == null) return 0.0D;
        if (stack.getItem() instanceof SwordItem) return 6.0D + getDurabilityTieBreaker(stack);
        if (stack.getItem() instanceof AxeItem) return 5.0D + getDurabilityTieBreaker(stack);
        return getDurabilityTieBreaker(stack);
    }

    public static double getMeleeSelectionScore(ItemStack stack) {
        return getMeleeDamage(stack);
    }

    public static double getBowDamage(ItemStack stack) {
        return stack != null && stack.getItem() instanceof BowItem ? 6.0D + getDurabilityTieBreaker(stack) : 0.0D;
    }

    public static double getBowSelectionScore(ItemStack stack) {
        return getBowDamage(stack);
    }

    public static double getToolSelectionScore(ItemStack stack) {
        if (stack == null) return 0.0D;
        if (stack.getItem() instanceof PickaxeItem) return 5.0D + getDurabilityTieBreaker(stack);
        if (stack.getItem() instanceof AxeItem) return 4.0D + getDurabilityTieBreaker(stack);
        if (stack.getItem() instanceof ShovelItem) return 3.0D + getDurabilityTieBreaker(stack);
        if (stack.getItem() instanceof HoeItem) return 2.0D + getDurabilityTieBreaker(stack);
        if (stack.getItem() instanceof ShearsItem) return 1.0D + getDurabilityTieBreaker(stack);
        return 0.0D;
    }

    public static double getAxeScore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof AxeItem ? getToolSelectionScore(stack) : 0.0D;
    }

    public static double getPickaxeScore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof PickaxeItem ? getToolSelectionScore(stack) : 0.0D;
    }

    public static double getShovelScore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ShovelItem ? getToolSelectionScore(stack) : 0.0D;
    }

    public static double getHoeScore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof HoeItem ? getToolSelectionScore(stack) : 0.0D;
    }

    public static double getShearsScore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ShearsItem ? getToolSelectionScore(stack) : 0.0D;
    }

    private static double getDurabilityTieBreaker(ItemStack stack) {
        if (stack == null || !stack.isDamageable()) return 0.0D;
        int maxDurability = stack.getMaxDamage();
        if (maxDurability <= 0) return 0.0D;
        int remainingDurability = maxDurability - stack.getDamage();
        return remainingDurability / (double) maxDurability / 1000.0D;
    }
}
