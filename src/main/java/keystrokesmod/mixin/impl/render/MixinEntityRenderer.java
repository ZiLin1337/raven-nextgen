package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.WorldRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinEntityRenderer {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderWorld", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
            opcode = Opcodes.GETFIELD, ordinal = 0))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci) {
        Raven.EVENT_BUS.post(new WorldRenderEvent(tickCounter.getTickDelta(false)));
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void onHurtCam(MatrixStack matrixStack, float f, CallbackInfo ci) {
        // NoHurtCam will cancel this
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(MatrixStack matrixStack, float f, CallbackInfo ci) {
        // NoBob will cancel this
    }

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHand(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
        // ItemRender hook for future use
    }
}
