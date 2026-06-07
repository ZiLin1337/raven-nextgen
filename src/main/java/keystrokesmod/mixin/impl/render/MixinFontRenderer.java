package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.NameHider;
import keystrokesmod.module.impl.render.AntiShuffle;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class MixinFontRenderer {
    @ModifyVariable(method = "drawLayer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyDrawLayerText(String text) {
        if (text == null) return null;
        if (ModuleManager.nameHider != null && ModuleManager.nameHider.isEnabled() {
            text = NameHider.getFakeName(text);
        }
        if (ModuleManager.antiShuffle != null && ModuleManager.antiShuffle.isEnabled() {
            text = AntiShuffle.removeObfuscation(text);
        }
        return text;
    }

    @ModifyVariable(method = "getWidth", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyGetWidthText(String text) {
        if (text == null) return null;
        if (ModuleManager.nameHider != null && ModuleManager.nameHider.isEnabled() {
            text = NameHider.getFakeName(text);
        }
        if (ModuleManager.antiShuffle != null && ModuleManager.antiShuffle.isEnabled() {
            text = AntiShuffle.removeObfuscation(text);
        }
        return text;
    }
}