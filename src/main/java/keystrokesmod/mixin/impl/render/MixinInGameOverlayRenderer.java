package keystrokesmod.mixin.impl.render;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderFireOverlay(
        net.minecraft.client.util.math.MatrixStack matrices,
        net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
        float tickDelta,
        CallbackInfo ci) {
        // NoRender模块 - 火焰覆盖
    }
}
