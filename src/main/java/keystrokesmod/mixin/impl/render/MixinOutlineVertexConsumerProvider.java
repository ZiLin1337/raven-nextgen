package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OutlineVertexConsumerProvider.class)
public class MixinOutlineVertexConsumerProvider {
    @Inject(method = "applyOutlineColor", at = @At("HEAD"))
    private void onApplyOutline(CallbackInfo ci) {
        // 轮廓渲染
        // 可用于ESP/Glow钩子
    }
}
