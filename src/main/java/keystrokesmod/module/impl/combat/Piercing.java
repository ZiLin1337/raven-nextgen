package keystrokesmod.module.impl.combat;

import com.google.common.base.Predicates;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntityFrame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;

public class Piercing extends Module {

    private SliderSetting sortMode;
    private ButtonSetting ignoreBlocks;
    private ButtonSetting ignoreTeamates;
    private ButtonSetting ignoreNonPlayer;
    private ButtonSetting weaponOnly;
    private ButtonSetting insideHitboxOnly;

    private int lastMouseOverTick = -1;

    private String[] sortModes = new String[] { "Hurt time", "Health" };

    public Piercing() {
        super("Piercing", category.combat);
        this.registerSetting(sortMode = new SliderSetting("Sort mode", 0, sortModes));
        this.registerSetting(ignoreBlocks = new ButtonSetting("Ignore blocks", false));
        this.registerSetting(ignoreNonPlayer = new ButtonSetting("Ignore non-players", true));
        this.registerSetting(ignoreTeamates = new ButtonSetting("Ignore teammates", true));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(insideHitboxOnly = new ButtonSetting("Inside hitbox only", false));
    }

    @Override
    public String getInfo() {
        return sortModes[(int) sortMode.getInput()];
    }

    public boolean shouldOverrideMouseOver() {
        if (!this.isEnabled()) {
            return false;
        }
        if (mc == null || mc.player == null || mc.world == null) {
            return false;
        }
        if (this.weaponOnly.isToggled() && !Utils.holdingWeapon()) {
            return false;
        }
        return ignoreBlocks.isToggled()
                || mc.crosshairTargetr == null
                || mc.crosshairTargetr.typeOfHit != HitResult.MovingObjectType.BLOCK;
    }

    public void modifyMouseOverFromGetMouseOver(float partialTicks) {
        if (!shouldOverrideMouseOver()) return;
        keystrokesmod$modifyMouseOverVanillaLook(partialTicks);
    }

    private void keystrokesmod$modifyMouseOverVanillaLook(final float partialTicks) {
        final Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null || mc.world == null) {
            return;
        }

        double reach = mc.interactionManager.getBlockReachDistance();
        final Vec3d eyes = viewEntity.getPositionEyes(partialTicks);
        if (mc.interactionManager.extendedReach()) {
            reach = 6.0;
        }
        final Vec3d look = viewEntity.getLook(partialTicks);
        final Vec3d rayEnd = eyes.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        Entity best = null;
        Vec3d bestHit = null;
        double bestDist = Double.MAX_VALUE;
        boolean bestLiving = false;
        int bestHurt = Integer.MAX_VALUE;
        float bestHp = Float.POSITIVE_INFINITY;
        final int modeSel = (int) this.sortMode.getInput();

        for (final Entity e : mc.world.getEntitiesInAABBexcluding(viewEntity,
                viewEntity.getEntityBoundingBox()
                        .addCoord(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach)
                        .expand(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith))) {
            if ((this.ignoreNonPlayer.isToggled() && !(e instanceof PlayerEntity)) || (this.ignoreTeamates.isToggled() && Utils.isTeamate(e))
                    || AntiBot.isBot(e) || (e instanceof PlayerEntity && Utils.isFriended((PlayerEntity) e))) {
                continue;
            }

            final float cb = e.getCollisionBorderSize();
            final Box bb = e.getEntityBoundingBox().expand(cb, cb, cb);
            final HitResult hit = bb.calculateIntercept(eyes, rayEnd);
            final boolean inside = bb.isVecInside(eyes);

            if (!inside && hit == null) continue;
            double dist = inside ? 0.0 : eyes.distanceTo(hit.hitVec);
            if (!mc.interactionManager.extendedReach() && dist > 3.0) continue;
            if (dist > reach) continue;
            if (dist >= bestDist) continue;
            if (this.insideHitboxOnly.isToggled() && dist > 0.10000000149011612) continue;

            if (e == viewEntity.ridingEntity && !viewEntity.canRiderInteract() && best != null) continue;

            boolean living = e instanceof LivingEntity;
            int hurt = living ? ((LivingEntity) e).hurtTime : Integer.MAX_VALUE;
            float hp = living ? ((LivingEntity) e).getHealth() : Float.POSITIVE_INFINITY;

            boolean take = false;
            if (best == null) {
                take = true;
            }
            else if (living && !bestLiving) {
                take = true;
            }
            else if (living == bestLiving) {
                if (!living) {
                    take = dist < bestDist;
                }
                else if (modeSel == 0) {
                    if (hurt < bestHurt) {
                        take = true;
                    }
                    else if (hurt == bestHurt && dist < bestDist) {
                        take = true;
                    }
                }
                else {
                    if (hp < bestHp) {
                        take = true;
                    }
                    else if (hp == bestHp && dist < bestDist) {
                        take = true;
                    }
                }
            }

            if (take) {
                best = e;
                bestHit = inside ? (hit == null ? eyes : hit.hitVec) : hit.hitVec;
                bestDist = dist;
                bestLiving = living;
                bestHurt = hurt;
                bestHp = hp;
            }
        }

        if (best != null && reach > 3.0 && bestDist > 3.0 && !mc.interactionManager.extendedReach()) {
            mc.crosshairTargetr = new HitResult(
                    HitResult.MovingObjectType.MISS, bestHit, null, new BlockPos(bestHit)
            );
            return;
        }

        if (best != null) {
            mc.crosshairTargetr = new HitResult(best, bestHit);
            if (best instanceof LivingEntity || best instanceof ItemEntityFrame) {
                mc.pointedEntity = best;
            }
        }
    }
}
