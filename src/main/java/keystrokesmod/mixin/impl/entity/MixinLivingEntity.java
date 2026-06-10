package keystrokesmod.mixin.impl.entity;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        // JumpEvent - will restore when event is available
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(net.minecraft.util.math.Vec3d movementInput, CallbackInfoReturnable<net.minecraft.util.math.Vec3d> cir) {
        // Movement hook
    }
}
