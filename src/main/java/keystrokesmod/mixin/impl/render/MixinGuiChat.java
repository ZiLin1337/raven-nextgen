package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.SendChatEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinGuiChat {

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, CallbackInfo ci) {
        if (message.startsWith("/")) return;
        SendChatEvent event = new SendChatEvent(message);
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}