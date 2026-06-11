package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.client.network.MultiplayerServerListPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListPinger.class)
public class MixinMultiplayerServerListPinger {
    @Inject(method = "addServerListEntry", at = @At("HEAD"))
    private void onAddServer(CallbackInfo ci) {
        // 服务器列表添加
        // 可用于PingSpoof模块
    }
}
