package keystrokesmod.mixin.impl.entity;

import keystrokesmod.Raven;
import keystrokesmod.event.ClientLookEvent;
import keystrokesmod.event.PlayerMoveEvent;
import keystrokesmod.event.StepHeightEvent;
import keystrokesmod.event.StrafeEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.SafeWalk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Math;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public double velocityX;
    @Shadow
    public double velocityZ;
    @Shadow
    public float yaw;

    @Shadow
    public abstract void setVelocity(double x, double y, double z);

    // ===== SafeWalk: modify the collision flag in Entity.move() =====
    @ModifyVariable(method = "move", at = @At(value = "STORE", ordinal = 0), name = "bl")
    private boolean injectSafeWalk(boolean original) {
        Entity self = (Entity) (Object) this;
        if (self != null && self == MinecraftClient.getInstance().player && self.isOnGround()) {
            if (SafeWalk.canSafeWalk()) {
                return true;
            }
        }
        return original;
    }

    // ===== moveFlying: restored method with StrafeEvent support =====
    @Unique
    public void moveFlying(float strafe, float forward, float friction) {
        Entity self = (Entity) (Object) this;
        StrafeEvent strafeEvent = new StrafeEvent(strafe, forward, friction, this.yaw);
        if (self == MinecraftClient.getInstance().player) {
            Raven.EVENT_BUS.post(strafeEvent);
        }

        strafe = strafeEvent.getStrafe();
        forward = strafeEvent.getForward();
        friction = strafeEvent.getFriction();
        float yaw = strafeEvent.getYaw();

        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = Math.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe *= f;
            forward *= f;

            float sinYaw = Math.sin(yaw * (float) Math.PI / 180.0F);
            float cosYaw = Math.cos(yaw * (float) Math.PI / 180.0F);
            this.velocityX += (double)(strafe * cosYaw - forward * sinYaw);
            this.velocityZ += (double)(forward * cosYaw + strafe * sinYaw);
        }
    }

    // ===== StepHeight redirect =====
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;method_58528()F"))
    private float redirectStepHeight(Entity instance) {
        StepHeightEvent stepHeightEvent = new StepHeightEvent(instance, instance.getStepHeight());
        Raven.EVENT_BUS.post(stepHeightEvent);
        return stepHeightEvent.stepHeight;
    }

    // ===== getRotationVector =====
    @Inject(method = "getRotationVector", at = @At("HEAD"), cancellable = true)
    private void onGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        ClientLookEvent event = new ClientLookEvent(yaw, pitch);
        Raven.EVENT_BUS.post(event);

        pitch = event.pitch;
        yaw = event.yaw;

        float f = Math.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f1 = Math.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f2 = -Math.cos(-pitch * (float) (Math.PI / 180.0));
        float f3 = Math.sin(-pitch * (float) (Math.PI / 180.0));

        cir.setReturnValue(new Vec3d(f1 * f2, f3, f * f2));
    }

    // ===== PlayerMoveEvent =====
    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MovementType type, double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ClientPlayerEntity) {
            Raven.EVENT_BUS.post(new PlayerMoveEvent(x, y, z));
        }
    }
}