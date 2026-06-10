package keystrokesmod.mixin.impl.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface IAccessorPlayerControllerMP {
    @Accessor("blockBreakingCooldown")
    int getBlockBreakingCooldown();
    @Accessor("blockBreakingCooldown")
    void setBlockBreakingCooldown(int cooldown);
}