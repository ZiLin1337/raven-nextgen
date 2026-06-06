package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "run", at = @At("HEAD"))
    private void onRun(CallbackInfo ci) {
        // Raven初始化
    }
    
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTickStart(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new GameTickEvent());
    }
    
    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void injectClickMouse(CallbackInfo ci) {
        // 处理点击事件
    }
}