package keystrokesmod.mixin.impl.block;

import net.minecraft.block.SlimeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeBlock.class)
public class MixinSlimeBlock {
    @Inject(method = "bounce", at = @At("HEAD"), cancellable = true)
    private void onBounce(Entity entity, CallbackInfo ci) {
        // NoSlow - 史莱姆块弹跳钩子
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        // NoSlow - 史莱姆块减速钩子
    }
}
