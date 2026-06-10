package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public class MixinLayerCape {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(
        net.minecraft.client.util.math.MatrixStack matrices,
        net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
        int light,
        PlayerEntityRenderState state,
        float f,
        float g,
        CallbackInfo ci) {
        // 披风渲染钩子 - CustomCapes模块可在此拦截
    }
}