package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void onUpdateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        // 方块破坏进度更新
        // 可用于FastBreak模块
    }

    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // 方块破坏事件
    }
}
