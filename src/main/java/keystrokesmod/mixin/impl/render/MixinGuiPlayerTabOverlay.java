package keystrokesmod.mixin.impl.render;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void nameHider$hideTabName(PlayerListEntry entry, CallbackInfoReturnable<String> cir) {
        // NameHider模块钩子 - 当模块实现后激活
    }
}
