package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PrePlayerInputEvent;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinMovementInputFromOptions {

    @Shadow @Final private GameOptions settings;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickPre(boolean slowDown, float tickDelta, CallbackInfo ci) {
        KeyboardInput self = (KeyboardInput)(Object)this;
        float forward = self.playerInput.forward() ? 1.0F : (self.playerInput.backward() ? -1.0F : 0.0F);
        float strafe = self.playerInput.left() ? 1.0F : (self.playerInput.right() ? -1.0F : 0.0F);
        boolean jump = self.playerInput.jump();
        boolean sneak = self.playerInput.sneak();

        PrePlayerInputEvent event = new PrePlayerInputEvent(forward, strafe, jump, sneak, slowDown ? 0.3D : 1.0D);
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickPost(boolean slowDown, float tickDelta, CallbackInfo ci) {
        KeyboardInput self = (KeyboardInput)(Object)this;
        Raven.EVENT_BUS.post(new PostPlayerInputEvent(
            self.playerInput.forward() ? 1.0F : (self.playerInput.backward() ? -1.0F : 0.0F),
            self.playerInput.left() ? 1.0F : (self.playerInput.right() ? -1.0F : 0.0F),
            self.playerInput.jump(),
            self.playerInput.sneak(),
            slowDown ? 0.3D : 1.0D
        ));
    }
}