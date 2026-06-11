package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(CallbackInfo ci) {
        // 键盘按键事件
        // 可用于KeyPressEvent模块
    }
}
