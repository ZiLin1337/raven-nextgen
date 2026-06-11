package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {
    @Inject(method = "beginRenderTick", at = @At("HEAD"))
    private void onBeginRenderTick(CallbackInfo ci) {
        // 渲染Tick开始
    }
}
