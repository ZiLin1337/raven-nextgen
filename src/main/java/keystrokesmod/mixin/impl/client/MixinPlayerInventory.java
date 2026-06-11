package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {
    @Inject(method = "swapSlot", at = @At("HEAD"))
    private void onSwapSlot(CallbackInfo ci) {
        // 物品栏交换
        // 可用于InventoryMove模块
    }
}
