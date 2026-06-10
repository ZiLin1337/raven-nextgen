package keystrokesmod.mixin.impl.entity;

import keystrokesmod.Raven;
import keystrokesmod.event.JumpEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(net.minecraft.entity.EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        keystrokesmod.Raven.EVENT_BUS.post(new JumpEvent());
        // will be extended later
    }
}
