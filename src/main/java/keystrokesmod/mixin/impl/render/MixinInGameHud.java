package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.client.event.impl.Render2DEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        Raven.EVENT_BUS.post(new Render2DEvent(context));
    }
}