package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(LivingEntity entity, float yaw, float tickDelta, net.minecraft.client.render.VertexConsumerProvider vertexConsumers, net.minecraft.client.util.math.MatrixStack matrices, int light, CallbackInfo ci) {
        // 实体渲染开始事件
        // 可用于ESP/Chams钩子
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderReturn(LivingEntity entity, float yaw, float tickDelta, net.minecraft.client.render.VertexConsumerProvider vertexConsumers, net.minecraft.client.util.math.MatrixStack matrices, int light, CallbackInfo ci) {
        // 实体渲染结束事件
    }
}
