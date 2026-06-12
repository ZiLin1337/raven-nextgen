package keystrokesmod.mixin.impl.gui;

import keystrokesmod.Raven;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHudLine.class)
public class MixinChatHudLine {
    @Inject(method = "draw", at = @At("HEAD"))
    private void onDraw(CallbackInfo ci) {
        // 聊天行绘制
        // 可用于BetterChat钩子
    }
}
