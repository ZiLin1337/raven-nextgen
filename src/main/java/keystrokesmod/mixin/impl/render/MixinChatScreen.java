package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        // 聊天屏幕初始化
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
        // 聊天屏幕按键事件
    }
}
