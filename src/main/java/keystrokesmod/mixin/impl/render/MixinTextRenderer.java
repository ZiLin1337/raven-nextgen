package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextRenderer.class)
public class MixinTextRenderer {
    @Inject(method = "draw", at = @At("HEAD"))
    private void onDraw(CallbackInfo ci) {
        // 文本渲染
        // 可用于NameHider模块
    }
}
