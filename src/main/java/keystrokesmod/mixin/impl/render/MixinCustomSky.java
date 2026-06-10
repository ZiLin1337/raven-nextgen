package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinCustomSky {

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void onRenderSky(net.minecraft.client.util.math.MatrixStack matrices, net.minecraft.client.render.VertexConsumerProvider.Immediate buffer, float tickDelta, boolean skipFog, CallbackInfo ci) {
        // Weather模块 - 自定义天空渲染钩子
    }
}