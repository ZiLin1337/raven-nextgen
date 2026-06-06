package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.Freelook;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class MixinActiveRenderInfo {
    @ModifyVariable(method = "update", at = @At(value = "STORE", ordinal = 0), name = "f")
    private float redirectYaw(float yaw) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled() && Freelook.perspectiveToggled) {
            return Freelook.cameraYaw;
        }
        return yaw;
    }

    @ModifyVariable(method = "update", at = @At(value = "STORE", ordinal = 1), name = "g")
    private float redirectPitch(float pitch) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled() && Freelook.perspectiveToggled) {
            return Freelook.cameraPitch;
        }
        return pitch;
    }
}