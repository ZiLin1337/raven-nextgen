package keystrokesmod.mixin.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {
    @Inject(method = "getSlipperiness", at = @At("RETURN"), cancellable = true)
    private void onGetSlipperiness(BlockState state, BlockView world, BlockPos pos, net.minecraft.entity.Entity entity, CallbackInfoReturnable<Float> cir) {
        // 可以添加NoSlow等模块的滑动性修改逻辑
    }

    @Inject(method = "isOpaque", at = @At("RETURN"), cancellable = true)
    private void onIsOpaque(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        // 可以添加Xray等模块的可见性修改逻辑
    }
}