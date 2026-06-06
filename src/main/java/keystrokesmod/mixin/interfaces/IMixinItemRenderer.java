package keystrokesmod.mixin.interfaces;

import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HeldItemRenderer.class)
public interface IMixinItemRenderer {
    @Accessor("mainHand")
    void setEquippedProgressMainHand(float progress);
    @Accessor("mainHand")
    float getEquippedProgressMainHand();
    @Accessor("offHand")
    void setEquippedProgressOffHand(float progress);
    @Accessor("offHand")
    float getEquippedProgressOffHand();
    @Accessor("itemStackMainHand")
    void setStackToRenderMainHand(net.minecraft.item.ItemStack stack);
    @Accessor("itemStackOffHand")
    void setStackToRenderOffHand(net.minecraft.item.ItemStack stack);
}
