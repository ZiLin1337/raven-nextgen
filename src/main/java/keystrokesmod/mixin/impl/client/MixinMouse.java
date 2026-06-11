package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(CallbackInfo ci) {
        // 鼠标滚轮事件
        // 可用于Zoom/HotbarScroll模块
    }
}
