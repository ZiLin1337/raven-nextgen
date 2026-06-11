package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public class MixinMobSpawnerBlockEntityRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // 刷怪笼渲染
        // 可用于StorageESP钩子
    }
}
