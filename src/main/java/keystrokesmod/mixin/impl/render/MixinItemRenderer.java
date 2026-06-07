package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntityEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinItemRenderer {
    @Shadow private ItemStack mainHand;
    @Shadow private float equipProgressMainHand;
    @Shadow private float prevEquipProgressMainHand;

    @Unique private boolean cancelUpdate = false;
    @Unique private boolean cancelReset = false;

    @Inject(method = "updateHeldItems", at = @At("HEAD"), cancellable = true)
    private void onUpdateHeldItems(CallbackInfo ci) {
        if (cancelUpdate)) {
            cancelUpdate = false;
            equipProgressMainHand = 1.0F;
            prevEquipProgressMainHand = 1.0F;
            ci.cancel();
        }
    }

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    private void onResetEquipProgress(Hand hand, CallbackInfo ci) {
        if (cancelReset)) {
            cancelReset = false;
            equipProgressMainHand = 1.0F;
            prevEquipProgressMainHand = 1.0F;
            ci.cancel();
        }
    }
}