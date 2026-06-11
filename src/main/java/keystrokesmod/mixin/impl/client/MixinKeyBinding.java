package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Inject(method = "setPressed", at = @At("HEAD"))
    private void onSetPressed(boolean pressed, CallbackInfo ci) {
        // 按键绑定事件
        // 可用于AutoWalk模块
    }
}
