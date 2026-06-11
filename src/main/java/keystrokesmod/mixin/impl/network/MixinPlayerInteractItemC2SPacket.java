package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractItemC2SPacket.class)
public class MixinPlayerInteractItemC2SPacket {
    @Inject(method = "getHand", at = @At("HEAD"))
    private void onGetHand(CallbackInfo ci) {
        // 获取交互手
        // 可用于KillAura模块
    }
}
