package keystrokesmod.mixin.interfaces;

import net.minecraft.item.ItemStack;

public interface IMixinItemRenderer {
    default void setEquippedProgressMainHand(float progress) {}
    default float getEquippedProgressMainHand() { return 0.0F; }
    default void setEquippedProgressOffHand(float progress) {}
    default float getEquippedProgressOffHand() { return 0.0F; }
    default void setStackToRenderMainHand(ItemStack stack) {}
    default void setStackToRenderOffHand(ItemStack stack) {}
    default void setCancelUpdate(boolean cancelUpdate) {}
    default void setCancelReset(boolean cancelReset) {}
    default boolean isItemRendererInUse() { return false; }
    default void setItemRendererInUse(boolean inUse) {}
}
