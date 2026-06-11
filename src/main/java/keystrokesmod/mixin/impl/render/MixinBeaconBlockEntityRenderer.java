package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntityRenderer.class)
public class MixinBeaconBlockEntityRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // 信标渲染
        // 可用于StorageESP钩子
    }
}
