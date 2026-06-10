package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class MixinGameSettings {

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CallbackInfo ci) {
        // 按键绑定加载后钩子
    }
}