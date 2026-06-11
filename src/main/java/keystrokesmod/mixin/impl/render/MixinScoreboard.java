package keystrokesmod.mixin.impl.render;

import keystrokesmod.Raven;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public class MixinScoreboard {
    @Inject(method = "updateDisplayObjective", at = @At("HEAD"))
    private void onUpdateDisplay(CallbackInfo ci) {
        // 计分板显示更新
        // 可用于HUD模块
    }
}
