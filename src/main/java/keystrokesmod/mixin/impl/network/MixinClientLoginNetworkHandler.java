package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {
    @Inject(method = "onLoginSuccess", at = @At("HEAD"))
    private void onLoginSuccess(CallbackInfo ci) {
        // 登录成功
    }
}
