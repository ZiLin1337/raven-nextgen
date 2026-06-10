package keystrokesmod.mixin.impl.render;

import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class MixinFontRenderer {
    @ModifyVariable(method = "drawLayer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyDrawLayerText(String text) {
        return text;
    }

    @ModifyVariable(method = "getWidth", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyGetWidthText(String text) {
        return text;
    }
}