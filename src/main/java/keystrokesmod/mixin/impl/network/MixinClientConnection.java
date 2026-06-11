package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        // 发送包事件
        // 可用于PacketC2S/PacketS2C模块
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(CallbackInfo ci) {
        // 接收包事件
    }
}
