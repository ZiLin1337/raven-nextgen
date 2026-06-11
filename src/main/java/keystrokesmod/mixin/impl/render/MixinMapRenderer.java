package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.MapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MixinMapRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // 地图渲染
        // 可用于MapHack钩子
    }
}
