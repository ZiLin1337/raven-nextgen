package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.Freelook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinRenderManager {

    @Shadow private float cameraYaw;
    @Shadow private float cameraPitch;

    @Unique private float raven$oldCameraYaw;
    @Unique private float raven$oldCameraPitch;

    @Inject(method = "updateRenderInfo", at = @At("HEAD"))
    private void onUpdateRenderInfo(net.minecraft.entity.Entity cameraEntity, CallbackInfo ci) {
        raven$oldCameraYaw = cameraYaw;
        raven$oldCameraPitch = cameraPitch;
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled() && Freelook.perspectiveToggled) {
            cameraYaw = Freelook.cameraYaw;
            cameraPitch = Freelook.cameraPitch;
        }
    }

    @Inject(method = "updateRenderInfo", at = @At("RETURN"))
    private void onUpdateRenderInfoReturn(net.minecraft.entity.Entity cameraEntity, CallbackInfo ci) {
        cameraYaw = raven$oldCameraYaw;
        cameraPitch = raven$oldCameraPitch;
    }
}