package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.hit.HitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraft {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getMouseOver(F)V", shift = At.Shift.BEFORE))
    public void onBeforeGetMouseOver(CallbackInfo ci) { }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getMouseOver(F)V", shift = At.Shift.AFTER))
    public void onRunTickMouseOver(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PostMouseSelectionEvent());
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/option/GameOptions;chatVisibility:Lnet/minecraft/entity/player/PlayerEntity$EnumChatVisibility;"))
    private void injectBeforeChatVisibility(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PrePlayerInteractEvent());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2))
    private void onRunTick(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PreInputEvent());
    }

    @Inject(method = "run", at = @At("HEAD"))
    public void onRun(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new RunGameLoopEvent());
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void injectClickMouse(CallbackInfo ci) {
        HitResult hit = mc.crosshairTarget;
        Raven.EVENT_BUS.post(new ClickMouseEvent());
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    public void injectRightClickMouse(CallbackInfo ci) {
        RightClickMouseEvent event = new RightClickMouseEvent();
        Raven.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    public void onRunTickStart(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new GameTickEvent());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void onRunTickAfterRightClickDelay(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new RightClickDelayTickEvent());
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onSetScreen(Screen guiScreen, CallbackInfo ci) {
        Screen previousGui = mc.currentScreen;
        Screen setGui = guiScreen;
        boolean opened = setGui != null;
        if (!opened) setGui = previousGui;
        Raven.EVENT_BUS.post(new GuiUpdateEvent(setGui, opened));
    }
}
