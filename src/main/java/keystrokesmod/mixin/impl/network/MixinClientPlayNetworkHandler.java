package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import keystrokesmod.event.PreEntityVelocityEvent;
import keystrokesmod.event.PreExplosionPacketEvent;
import keystrokesmod.module.ModuleManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPosLookPre(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ModuleManager.noRotate != null)) {
            ModuleManager.noRotate.handlePlayerPosLookPre();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    private void onPlayerPosLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ModuleManager.noRotate != null)) {
            ModuleManager.noRotate.handlePlayerPosLook(packet);
        }
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        PreEntityVelocityEvent event = new PreEntityVelocityEvent(
                null, 0, 0, 0
        );
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        PreExplosionPacketEvent event = new PreExplosionPacketEvent();
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}