package keystrokesmod.mixin.impl.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinRenderPlayer {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderHead(
        net.minecraft.client.render.entity.state.PlayerEntityRenderState state,
        net.minecraft.client.util.math.MatrixStack matrices,
        net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci) {
        // 玩家渲染钩子 - 物品伪造/BlockAnimation模块
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void onRenderTail(
        net.minecraft.client.render.entity.state.PlayerEntityRenderState state,
        net.minecraft.client.util.math.MatrixStack matrices,
        net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci) {
        // 渲染结束钩子
    }
}