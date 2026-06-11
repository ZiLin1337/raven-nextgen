package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, CallbackInfo ci) {
        // 聊天消息添加事件
        // 可用于BetterChat/聊天过滤
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void onClear(CallbackInfo ci) {
        // 聊天清除事件
    }
}
