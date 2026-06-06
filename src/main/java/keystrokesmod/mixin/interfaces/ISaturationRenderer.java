package keystrokesmod.mixin.interfaces;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface ISaturationRenderer {
    @Accessor("visibleHeldItemTooltips")
    void setVisibleHeldItemTooltips(boolean visible);
    
    @Accessor("visibleHeldItemTooltips")
    boolean getVisibleHeldItemTooltips();

    @Accessor("heldItemTooltipFade")
    void setHeldItemTooltipFade(int fade);
    
    @Accessor("heldItemTooltipFade")
    int getHeldItemTooltipFade();
}
