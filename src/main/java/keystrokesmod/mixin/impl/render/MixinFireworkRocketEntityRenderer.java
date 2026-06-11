package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.entity.FireworkRocketEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntityRenderer.class)
public class MixinFireworkRocketEntityRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // 烟花渲染
        // 可用于ESP钩子
    }
}
