package keystrokesmod.mixin.impl.client;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for FlowableFluid
 * Used by Jesus module to enable walking on water/lava
 */
@Mixin(FlowableFluid.class)
public class MixinFlowableFluid {
    @Inject(method = "getVelocity", at = @At("HEAD"), cancellable = true)
    private void onGetVelocity(World world, BlockPos pos, FluidState state, CallbackInfoReturnable<Vec3d> cir) {
        // Hook for Jesus velocity modification
    }

    @Inject(method = "canBeReplacedWith", at = @At("HEAD"), cancellable = true)
    private void onCanBeReplacedWith(World world, BlockPos pos, FluidState state, CallbackInfoReturnable<Boolean> cir) {
        // Hook for collision modification
    }
}
