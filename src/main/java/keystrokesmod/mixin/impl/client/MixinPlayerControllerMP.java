package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinPlayerControllerMP {
    @Shadow private int blockBreakingCooldown;

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PreAttackEvent(null));
    }

    @Inject(method = "attackEntity", at = @At("RETURN"))
    private void onPostAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PostAttackEvent(target));
    }

    @Redirect(
        method = "updateBlockBreakingProgress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"
        )
    )
    private float fastMineScaleHardness(BlockState state, PlayerEntity player, net.minecraft.world.BlockView world, BlockPos pos) {
        float hardness = state.calcBlockBreakingDelta(player, world, pos);
        return hardness;
    }

    @Unique
    private void raven$fastMineApplyBreakDelaySlider() {
        // FastMine delay override - will restore when module is available
    }

    @Inject(
        method = "attackBlock",
        at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 0, shift = At.Shift.AFTER)
    )
    private void raven$fastMineAfterClickBlockSetDelay(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        raven$fastMineApplyBreakDelaySlider();
    }

    @Inject(
        method = "updateBlockBreakingProgress",
        at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 0, shift = At.Shift.AFTER)
    )
    private void raven$fastMineAfterCreativeMiningSetDelay(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        raven$fastMineApplyBreakDelaySlider();
    }
}
