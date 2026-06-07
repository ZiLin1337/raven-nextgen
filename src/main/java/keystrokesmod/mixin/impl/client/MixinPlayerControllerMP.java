package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.event.AttackEvent;
import keystrokesmod.event.UseItemEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.BedAura;
import keystrokesmod.module.impl.player.FastMine;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    @Shadow private int blockBreakCooldown;

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEvent event = new AttackEvent(target);
        Raven.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Unique
    private void raven$fastMineApplyBreakDelay() {
        BedAura bedAura = ModuleManager.bedAura;
        if (bedAura != null && bedAura.shouldOverrideFastMine()) {
            int delay = bedAura.getBreakDelayTicks();
            if (delay < 5)) {
                this.blockBreakCooldown = delay;
            }
            return;
        }
        FastMine fm = ModuleManager.fastMine;
        if (fm == null) return;
        int o = fm.getBlockHitDelayOverrideOrMinusOne();
        if (o >= 0)) {
            this.blockBreakCooldown = o;
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setBlockBreakingCooldown(I)V", shift = At.Shift.AFTER))
    private void afterAttackBlockSetDelay(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        raven$fastMineApplyBreakDelay();
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setBlockBreakingCooldown(I)V", shift = At.Shift.AFTER))
    private void afterUpdateBlockProgressSetDelay(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        raven$fastMineApplyBreakDelay();
    }

    @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setBlockBreakingCooldown(I)V", shift = At.Shift.AFTER))
    private void afterBreakBlockSetDelay(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        raven$fastMineApplyBreakDelay();
    }

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private float fastMineScaleHardness(BlockState state, PlayerEntity player, Object world, BlockPos pos) {
        float hardness = state.calcBlockBreakingDelta(player, player.getWorld(), pos);
        BedAura bedAura = ModuleManager.bedAura;
        if (bedAura != null && bedAura.shouldOverrideFastMine()) {
            return hardness * bedAura.getBreakSpeedMultiplier();
        }
        FastMine fm = ModuleManager.fastMine;
        if (fm == null) return hardness;
        return hardness * fm.getBreakSpeedMultiplier();
    }
}