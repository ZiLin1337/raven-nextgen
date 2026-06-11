package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class MixinItem {
    @Inject(method = "getMaxDamage", at = @At("HEAD"))
    private void onGetMaxDamage(CallbackInfo ci) {
        // 获取最大伤害
        // 可用于Durability模块
    }
}
