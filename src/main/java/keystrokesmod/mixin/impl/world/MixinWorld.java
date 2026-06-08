package keystrokesmod.mixin.impl.world;

import keystrokesmod.module.ModuleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class MixinWorld {
    @Inject(method = "getThunderStrength", at = @At("RETURN"), cancellable = true)
    private void setThunderStrength(float delta, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.weather != null && ModuleManager.weather.isEnabled() && ModuleManager.weather.lightning.getInput() > 0) {
            cir.setReturnValue((float) ModuleManager.weather.lightning.getInput());
        }
    }

    @Inject(method = "getRainStrength", at = @At("RETURN"), cancellable = true)
    private void setRainStrength(float delta, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.weather != null && ModuleManager.weather.isEnabled() && ModuleManager.weather.rain.isToggled()) {
            cir.setReturnValue(1F);
        }
    }
}