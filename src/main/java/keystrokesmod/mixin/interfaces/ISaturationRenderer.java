package keystrokesmod.mixin.interfaces;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface ISaturationRenderer {
    @Accessor("visibleHeldToolItemtips")
    void setVisibleHeldToolItemtips(boolean visible);
    
    @Accessor("visibleHeldToolItemtips")
    boolean getVisibleHeldToolItemtips();

    @Accessor("heldToolItemtipFade")
    void setHeldToolItemtipFade(int fade);
    
    @Accessor("heldToolItemtipFade")
    int getHeldToolItemtipFade();
}
