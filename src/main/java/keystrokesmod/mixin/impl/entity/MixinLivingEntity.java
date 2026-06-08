package keystrokesmod.mixin.impl.entity;

import keystrokesmod.Raven;
import keystrokesmod.event.JumpEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerMovementInputEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.utility.RotationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow public float headYaw;
    @Shadow public float bodyYaw;
    @Shadow public float handSwingProgress;
    @Shadow public float lastHandSwingProgress;

    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);
    @Shadow protected abstract float getJumpVelocity();

    // ===== updateDistance (render yaw control for KillAura / rotation) =====
    @Inject(method = "updateLimbs", at = @At("HEAD"), cancellable = true)
    private void injectUpdateLimbs(float yawOffset, float yawPitchDiff, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        float rotationYaw = self.getYaw();

        if (Settings.fullBody != null && Settings.rotateBody != null
                && !Settings.fullBody.isToggled() && Settings.rotateBody.isToggled()
                && self instanceof ClientPlayerEntity && PreMotionEvent.setRenderYaw()) {
            if (this.handSwingProgress > 0F) {
                rotationYaw = RotationUtils.renderYaw;
            }
            rotationYaw = RotationUtils.renderYaw;
            this.headYaw = RotationUtils.renderYaw;
        }

        float f = Math.wrapDegrees(yawOffset - this.bodyYaw);
        this.bodyYaw += f * 0.3F;
        float f1 = Math.wrapDegrees(rotationYaw - this.bodyYaw);
        boolean flag = f1 < -90.0F || f1 >= 90.0F;

        if (f1 < -75.0F) f1 = -75.0F;
        if (f1 >= 75.0F) f1 = 75.0F;

        this.bodyYaw = rotationYaw - f1;
        if (f1 * f1 > 2500.0F) this.bodyYaw += f1 * 0.2F;
        if (flag) yawPitchDiff *= -1.0F;

        cir.setReturnValue(yawPitchDiff);
    }

    // ===== playerJump (JumpEvent) =====
    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        JumpEvent jumpEvent = new JumpEvent(this.getJumpVelocity(), self.getYaw(), self.isSprinting());
        Raven.EVENT_BUS.post(jumpEvent);
        if (jumpEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        self.setVelocity(self.getVelocity().x, jumpEvent.getMotionY(), self.getVelocity().z);

        if (jumpEvent.applySprint()) {
            float f = jumpEvent.getYaw() * 0.017453292F;
            self.addVelocity(-Math.sin(f) * 0.2F, 0, Math.cos(f) * 0.2F);
        }
    }

    // ===== isPotionActive (AntiDebuff) =====
    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.antiDebuff != null && ModuleManager.antiDebuff.isEnabled()) {
            if ((effect == Registries.STATUS_EFFECT.get(net.minecraft.util.Identifier.ofVanilla("nausea"))
                    && ModuleManager.antiDebuff.removeNausea.isToggled())
                || (effect == Registries.STATUS_EFFECT.get(net.minecraft.util.Identifier.ofVanilla("blindness"))
                    && ModuleManager.antiDebuff.removeBlindness.isToggled())) {
                if (ModuleManager.antiDebuff.removeSideEffects.isToggled()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    // ===== travel (PrePlayerMovementInputEvent) =====
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"))
    private void onTravelRedirect(LivingEntity self, Vec3d movementInput) {
        if (self instanceof ClientPlayerEntity) {
            PrePlayerMovementInputEvent event = new PrePlayerMovementInputEvent(
                    (float) movementInput.z, // forward is on z
                    (float) movementInput.x  // strafe is on x
            );
            Raven.EVENT_BUS.post(event);

            // Apply modified input
            self.travel(new Vec3d(event.strafe, 0, event.forward));
        } else {
            self.travel(movementInput);
        }
    }
}