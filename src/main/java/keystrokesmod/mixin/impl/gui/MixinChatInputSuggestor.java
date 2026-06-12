package keystrokesmod.mixin.impl.gui;

import keystrokesmod.Raven;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.class)
public class MixinChatInputSuggestor {
    @Inject(method = "showSuggestions", at = @At("HEAD"))
    private void onShowSuggestions(CallbackInfo ci) {
        // 显示建议
        // 可用于AutoComplete模块
    }
}
