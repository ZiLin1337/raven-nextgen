package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        // 光照更新
        // 可用于FullBright模块
    }
}
