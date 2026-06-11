package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderItem(CallbackInfo ci) {
        // 手持物品渲染
        // 可用于NoSlow/Swing模块
    }
}
