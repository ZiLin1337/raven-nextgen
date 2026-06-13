package keystrokesmod.mixin.impl.fluid;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowableFluid.class)
public abstract class MixinFlowableFluid {
    /**
     * Hook for Jesus module to handle fluid physics
     */
    public int getFlowVelocity(World world, BlockPos pos, FluidState state) {
        // Default implementation
        return 0;
    }
}
