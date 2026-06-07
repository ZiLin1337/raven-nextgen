package keystrokesmod.script.model;

import keystrokesmod.Raven;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemStackModel {
    public int count;
    public int maxCount;
    public int maxDamage;
    public int damage;
    public String name;
    public String displayName;
    public String type;
    public boolean isBlock;
    public String unlocalizedName;

    public ItemStackModel(ItemStack itemStack) {
        this.count = itemStack.getCount();
        this.maxCount = itemStack.getMaxCount();
        this.name = itemStack.getItem().getTranslationKey();
        this.displayName = itemStack.getName().getString();
        this.type = itemStack.getItem().getClass().getSimpleName();
        this.isBlock = itemStack.getItem() instanceof net.minecraft.item.BlockItem;
        this.unlocalizedName = itemStack.getItem().getTranslationKey();
    }

    public int getCount() { return count; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public boolean isBlock() { return isBlock; }

    public java.util.List<String> getTooltip() {
        return itemStack.getTooltip(Raven.mc.player, net.minecraft.item.tooltip.TooltipType.BASIC)
                .stream().map(Text::getString).toList();
    }

    private final ItemStack itemStack;
    public ItemStackModel(ItemStack stack, boolean full) {
        this(stack);
        this.itemStack = stack;
    }

    @Override
    public String toString() {
        return "ItemStack(" + name + " x" + count + ")";
    }
}
