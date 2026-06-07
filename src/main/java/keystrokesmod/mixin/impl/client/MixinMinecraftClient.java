package keystrokesmod.mixin.impl.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTick(CallbackInfo ci) {
        // Tick event
    }
    
    @Inject(method = "runTick", at = @At("RETURN"))
    private void onRunTickEnd(CallbackInfo ci) {
        // Tick end event
    }
}
