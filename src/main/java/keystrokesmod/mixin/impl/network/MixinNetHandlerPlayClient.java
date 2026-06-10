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
public class MixinNetHandlerPlayClient {

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    public void onPlayerPositionLookPre(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ModuleManager.noRotate != null) {
            ModuleManager.noRotate.onPlayerPositionLookPre();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ModuleManager.noRotate != null) {
            ModuleManager.noRotate.onPlayerPositionLook(packet);
        }
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        PreEntityVelocityEvent event = new PreEntityVelocityEvent(packet);
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    public void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        PreExplosionPacketEvent event = new PreExplosionPacketEvent(packet);
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}