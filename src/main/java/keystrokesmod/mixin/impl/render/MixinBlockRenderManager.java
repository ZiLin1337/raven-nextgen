package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {
    @Inject(method = "renderBlock", at = @At("HEAD"))
    private void onRenderBlock(CallbackInfo ci) {
        // 方块渲染
        // 可用于StorageESP钩子
    }
}
