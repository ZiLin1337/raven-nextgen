package keystrokesmod.mixin.impl.render;

import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Inject(method = "applyFog", at = @At("HEAD"))
    private static void onApplyFog(
        net.minecraft.client.render.Camera camera,
        BackgroundRenderer.FogType fogType,
        org.joml.Vector4f fogColor,
        float viewDistance,
        boolean thickFog,
        float tickDelta,
        CallbackInfo ci) {
        // NoRender/Fog模块 - 自定义雾效
    }
}
