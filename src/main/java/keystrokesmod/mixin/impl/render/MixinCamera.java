package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.Freelook;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow private Vec3d pos;
    @Shadow private boolean thirdPerson;
    @Shadow private float yaw;
    @Shadow private float pitch;
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow protected abstract float clipToSpace(float f);
    @Shadow protected abstract void moveBy(float f, float g, float h);
    @Shadow public abstract void setPos(Vec3d pos);

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled() && Freelook.perspectiveToggled) {
            this.thirdPerson = true;
            setRotation(Freelook.cameraYaw, Freelook.cameraPitch);
            moveBy(-clipToSpace(4.0F), 0.0f, 0.0f);
            ci.cancel();
        }
    }
}
