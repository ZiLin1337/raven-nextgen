package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen {
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        // 断开连接屏幕初始化
        // 可用于AntiDisconnect模块
    }
}
