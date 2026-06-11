package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandshakeC2SPacket.class)
public class MixinHandshakeC2SPacket {
    @Inject(method = "getAddress", at = @At("HEAD"))
    private void onGetAddress(CallbackInfo ci) {
        // 获取地址
        // 可用于AntiStaff模块
    }
}
