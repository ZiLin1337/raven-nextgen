package keystrokesmod.mixin.impl.entity;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        if (((Object) this) instanceof net.minecraft.client.network.ClientPlayerEntity) {
            PreAttackEvent event = new PreAttackEvent(target);
            Raven.EVENT_BUS.post(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttackPost(Entity target, CallbackInfo ci) {
        if (((Object) this) instanceof net.minecraft.client.network.ClientPlayerEntity) {
            Raven.EVENT_BUS.post(new PostAttackEvent(target));
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(net.minecraft.entity.damage.DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof net.minecraft.client.network.ClientPlayerEntity) {
            Raven.EVENT_BUS.post(new EntityAttackEvent((net.minecraft.entity.Entity)(Object)this));
        }
    }
}
