package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractBlockC2SPacket.class)
public class MixinPlayerInteractBlockC2SPacket {
    @Inject(method = "getBlockHitResult", at = @At("HEAD"))
    private void onGetHitResult(CallbackInfo ci) {
        // 获取方块命中结果
        // 可用于Reach/KillAura模块
    }
}
