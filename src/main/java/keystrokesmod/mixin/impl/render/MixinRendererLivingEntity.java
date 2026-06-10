package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.*;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinRendererLivingEntity {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderHead(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModuleManager.playerESP != null && ModuleManager.playerESP.isEnabled()) {
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1100000.0f);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void onRenderTail(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModuleManager.playerESP != null && ModuleManager.playerESP.isEnabled()) {
            glPolygonOffset(1.0f, 1100000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);
        }
    }
}