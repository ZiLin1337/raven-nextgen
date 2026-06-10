package keystrokesmod.mixin.impl.render;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinGuiContainer {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void raven$cancelManagedInventoryMouseClick(double mouseX, double mouseY, int mouseButton, CallbackInfo ci) {
        // InvManager模块钩子 - 当模块实现后激活
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void raven$cancelManagedInventoryMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfo ci) {
        // InvManager模块钩子
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void raven$cancelManagedInventoryMouseRelease(double mouseX, double mouseY, int mouseButton, CallbackInfo ci) {
        // InvManager模块钩子
    }
}
