package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(CallbackInfo ci) {
        // 游戏消息
        // 可用于ChatFilter模块
    }
}
