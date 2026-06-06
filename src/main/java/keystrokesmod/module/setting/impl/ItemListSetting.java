package keystrokesmod.module.setting.impl;

import net.minecraft.item.ItemStack;
import java.util.List;

public class ItemListSetting extends BlockListSetting {
    public ItemListSetting(String name) { super(name); }
    public ItemListSetting(String name, String... legacyProfileKeys) { super(name, legacyProfileKeys); }
    public ItemListSetting(GroupSetting group, String name) { super(group, name); }
    public ItemListSetting(GroupSetting group, String name, String... legacyProfileKeys) { super(group, name, legacyProfileKeys); }
    public void addItem(String id) { addBlock(id); }
    public void removeItem(String id) { removeBlock(id); }
    public List<String> getItems() { return getBlocks(); }
    public boolean containsItem(String id) { return contains(id); }
    public boolean matches(ItemStack stack) { return true; }
}
