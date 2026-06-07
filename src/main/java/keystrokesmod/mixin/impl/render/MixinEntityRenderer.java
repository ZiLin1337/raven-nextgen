package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.Freelook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinEntityRenderer {
    @Shadow private float tickDelta;

    @Redirect(method = "tiltScreenWhenHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/camera/Camera;getRoll()F"))
    private float injectNoHurtCam(Camera camera) {
        if (ModuleManager.noHurtCam != null && ModuleManager.noHurtCam.isEnabled() {
            return (float) (camera.getRoll() / 14 * ModuleManager.noHurtCam.multiplier.getInput());
        }
        return camera.getRoll();
    }

    @ModifyVariable(method = "renderWorld", at = @At("HEAD"), argsOnly = true)
    private float injectFreelookYaw(float tickDelta) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled() && Freelook.perspectiveToggled) {
            return tickDelta;
        }
        return tickDelta;
    }

    @Redirect(method = "tiltScreenWhenHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean redirectNausea(ClientPlayerEntity player, Object effect) {
        if (ModuleManager.antiDebuff != null && ModuleManager.antiDebuff.canRemoveNausea(effect) {
            return false;
        }
        return player.hasStatusEffect((net.minecraft.entity.effect.StatusEffect) effect);
    }

    @Redirect(method = "renderFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean redirectFogEffect(ClientPlayerEntity player, Object effect) {
        if (ModuleManager.antiDebuff != null && ModuleManager.antiDebuff.canRemoveBlindness(effect) {
            return false;
        }
        return player.hasStatusEffect((net.minecraft.entity.effect.StatusEffect) effect);
    }
}