package keystrokesmod.mixin.impl.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keystrokesmod.Raven;
import keystrokesmod.event.ClientLookEvent;
import keystrokesmod.event.PlayerMoveEvent;
import keystrokesmod.event.StepHeightEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow public float stepHeight;

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MovementType type, Vec3d movement, CallbackInfo ci) {
        if (((Object) this) instanceof ClientPlayerEntity) {
            Raven.EVENT_BUS.post(new PlayerMoveEvent(movement.x, movement.y, movement.z));
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (((Object) this) != MinecraftClient.getInstance().player) return;
        Raven.EVENT_BUS.post(new ClientLookEvent(0, 0));
    }

    @ModifyExpressionValue(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F"))
    private float modifyStepHeight(float originalStepHeight) {
        if (!(((Object) this) instanceof ClientPlayerEntity)) return originalStepHeight;
        StepHeightEvent event = new StepHeightEvent(originalStepHeight);
        Raven.EVENT_BUS.post(event);
        return event.getStepHeight();
    }
}