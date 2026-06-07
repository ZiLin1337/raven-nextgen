package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.Freelook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BufferBuilder.class)
public class MixinRenderGlobal {
    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw(F)F"))
    private float redirectSetupTerrainYaw(Entity entity, float tickDelta) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled()
                && Freelook.perspectiveToggled && entity == MinecraftClient.getInstance().getCameraEntity()) {
            return Freelook.cameraYaw;
        }
        return entity.getYaw(tickDelta);
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPitch(F)F"))
    private float redirectSetupTerrainPitch(Entity entity, float tickDelta) {
        if (ModuleManager.freelook != null && ModuleManager.freelook.isEnabled()
                && Freelook.perspectiveToggled && entity == MinecraftClient.getInstance().getCameraEntity()) {
            return Freelook.cameraPitch;
        }
        return entity.getPitch(tickDelta);
    }
}