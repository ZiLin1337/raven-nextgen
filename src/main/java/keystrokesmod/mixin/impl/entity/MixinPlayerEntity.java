package keystrokesmod.mixin.impl.entity;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.Reduce;
import keystrokesmod.module.impl.movement.KeepSprint;
import keystrokesmod.utility.BlockAnimationUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
// Removed Math import
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
    @Shadow public abstract ItemStack getMainHandStack();
    @Shadow public abstract ItemStack getOffHandStack();
    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow
    public abstract void clearLastAttackedEntity();

    // ===== Attack target entity with current item - port of 1.8.9 attackTargetEntityWithCurrentItem =====
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        // Only intercept client-side attack
        if (self.getWorld().isClient()) {
            if (target.canHit() && !target.handleAttack(self)) {
                float attackDamage = (float) self.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
                int knockback = 0;
                float enchantDamage = 0;

                if (target instanceof LivingEntity) {
                    enchantDamage = EnchantmentHelper.getAttackDamage(self.getMainHandStack(),
                            ((LivingEntity) target).getGroup());
                }

                knockback += EnchantmentHelper.getKnockback(self);
                if (self.isSprinting()) {
                    knockback++;
                }

                if (attackDamage > 0.0F || enchantDamage > 0.0F) {
                    boolean critical = self.fallDistance > 0.0F && !self.isOnGround()
                            && !self.isClimbing() && !self.isTouchingWater()
                            && !self.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS)
                            && self.getVehicle() == null && target instanceof LivingEntity;
                    if (critical && attackDamage > 0.0F) {
                        attackDamage *= 1.5F;
                    }
                    attackDamage += enchantDamage;

                    boolean fireAspect = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(self);
                    if (target instanceof LivingEntity && fireAspectLevel > 0 && !target.isOnFire()) {
                        fireAspect = true;
                        target.setFireTicks(1);
                    }

                    double d0 = target.getVelocity().x;
                    double d1 = target.getVelocity().y;
                    double d2 = target.getVelocity().z;

                    boolean attacked = target.damage(
                            DamageSource.player(self), attackDamage);
                    if (attacked) {
                        if (knockback > 0) {
                            target.addVelocity(
                                    (double) (-Math.sin(self.getYaw() * (float) Math.PI / 180.0F)
                                            * (float) knockback * 0.5F),
                                    0.1,
                                    (double) (Math.cos(self.getYaw() * (float) Math.PI / 180.0F)
                                            * (float) knockback * 0.5F));

                            if (ModuleManager.reduce != null && ModuleManager.reduce.isEnabled()) {
                                Reduce.reduce(target);
                            } else if (ModuleManager.keepSprint != null && ModuleManager.keepSprint.isEnabled()) {
                                KeepSprint.keepSprint(target);
                            } else {
                                self.setVelocity(self.getVelocity().x * 0.6D,
                                        self.getVelocity().y,
                                        self.getVelocity().z * 0.6D);
                                self.setSprinting(false);
                            }
                        }

                        if (target instanceof ServerPlayerEntity && target.isVelocityChanged()) {
                            ((ServerPlayerEntity) target).networkHandler.sendPacket(
                                    new EntityVelocityUpdateS2CPacket(target));
                            target.setVelocityChanged(false);
                            target.setVelocity(d0, d1, d2);
                        }

                        if (critical) {
                            self.addCritParticles(target);
                        }
                        if (enchantDamage > 0.0F) {
                            self.addEnchantedHitParticles(target);
                        }
                        if (attackDamage >= 18.0F) {
                            // achievement removed in later versions
                        }
                        self.setLastAttackedEntity(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.onTargetDamaged(self, target,
                                    self.getWorld(), self.getMainHandStack());
                        }

                        EnchantmentHelper.applyArthropodEnchantments(self, target);
                        ItemStack item = self.getMainHandStack();
                        if (!item.isEmpty() && target instanceof LivingEntity) {
                            item.postHit((LivingEntity) target, self);
                            if (item.isEmpty()) {
                                self.setStackInHand(self.getActiveHand(), ItemStack.EMPTY);
                            }
                        }
                        if (target instanceof LivingEntity) {
                            self.addExhaustion(0.3F);
                        }
                    } else if (fireAspect) {
                        target.extinguishWithSound();
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }

    // ===== isBlocking (BlockAnimationUtils) =====
    @Inject(method = "isBlocking", at = @At("RETURN"), cancellable = true)
    private void isBlocking(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && BlockAnimationUtils.shouldForceBlockAnimation(
                (PlayerEntity) (Object) this, this.getMainHandStack())) {
            cir.setReturnValue(true);
        }
    }
}