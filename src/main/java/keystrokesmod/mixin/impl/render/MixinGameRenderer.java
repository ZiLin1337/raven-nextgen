package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.RenderTickEvent;
import keystrokesmod.event.TickEvent;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(net.minecraft.util.profiler.Profiler profiler, float tickDelta, long startTime, boolean renderBlockOutline, CallbackInfo ci) {
        Raven.EVENT_BUS.post(new RenderTickEvent(TickEvent.Phase.START, tickDelta));
    }
}
