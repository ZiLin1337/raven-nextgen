package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class MixinGameSettings {
    @Inject(method = "write", at = @At("HEAD"))
    private void onWrite(CallbackInfo ci) {
        // 游戏设置保存
    }
}
