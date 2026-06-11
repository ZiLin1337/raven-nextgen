package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @Inject(method = "getCount", at = @At("HEAD"))
    private void onGetCount(CallbackInfo ci) {
        // 获取物品数量
        // 可用于ItemESP模块
    }
}
