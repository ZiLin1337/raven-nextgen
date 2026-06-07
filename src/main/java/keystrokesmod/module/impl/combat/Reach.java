package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.ClickMouseEvent;
import keystrokesmod.event.PreAttackEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3dd;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Reach extends Module {
    public static SliderSetting min;
    public static SliderSetting max;
    public static ButtonSetting weaponOnly;
    public static ButtonSetting movingOnly;
    public static ButtonSetting sprintOnly;
    public static ButtonSetting hitThroughBlocks;
    public static Reach instance;

    public Reach() {
        super("Reach", category.combat);
        instance = this;
        this.registerSetting(min = new SliderSetting("Min", 3.1, 3.0, 6.0, 0.05));
        this.registerSetting(max = new SliderSetting("Max", 3.3, 3.0, 6.0, 0.05));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(movingOnly = new ButtonSetting("Moving only", false));
        this.registerSetting(sprintOnly = new ButtonSetting("Sprint only", false));
        this.registerSetting(hitThroughBlocks = new ButtonSetting("Hit through blocks", false));
    }

    @Override
    public void guiUpdate() {
        if (min != null && max != null)) {
            if (min.getInput() > max.getInput()) {
                max.setValue(min.getInput());
            }
        }
    }

    @EventHandler
    public void onPreAttack(PreAttackEvent e) {
        if (!Utils.nullCheck()) return;
        if (!checkConditions()) return;

        double reachVal = getReachValue();
        Object[] hitResult = getEntityHit(reachVal, 0.0);
        if (hitResult == null) return;

        Entity target = (Entity) hitResult[0];
        Vec3dd hitVec = (Vec3dd) hitResult[1];

        // Override the crosshair target for this attack
        e.setTarget(target);
        e.setHitVec(hitVec);
        e.setReachModified(true);
    }

    private boolean checkConditions() {
        if (weaponOnly.isToggled() && !Utils.holdingWeapon()) return false;
        if (movingOnly.isToggled() && !Utils.isMoving()) return false;
        if (sprintOnly.isToggled() && !mc.player.isSprinting()) return false;
        return true;
    }

    private double getReachValue() {
        double val = Utils.getRandomValue(min.getInput(), max.getInput(), Math.random());
        return Math.min(val, 6.0);
    }

    public static Object[] getEntityHit(double reach, double expand) {
        if (mc.player == null || mc.world == null) return null;
        if (!ModuleManager.moduleManager.getModule("Reach").isEnabled()) {
            reach = 3.0;
        }
        return rayCastEntities(reach, expand, null);
    }

    public static Object[] rayCastEntities(double reach, double expand, float[] rotations) {
        if (mc.player == null || mc.world == null) return null;

        Vec3dd eyePos = mc.player.getEyePos();
        Vec3dd lookVec;
        if (rotations != null)) {
            float radYaw = -rotations[0] * 0.017453292f - (float)Math.PI;
            float radPitch = -rotations[1] * 0.017453292f;
            float cosYaw = (float)Math.cos(radYaw);
            float sinYaw = (float)Math.sin(radYaw);
            float cosPitch = -(float)Math.cos(radPitch);
            float sinPitch = (float)Math.sin(radPitch);
            lookVec = new Vec3dd(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
        } else {
            lookVec = mc.player.getRotationVec(1.0f);
        }

        Vec3dd endPos = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);

        // Check block collision
        BlockHitResult blockHit = mc.world.raycast(new RaycastContext(
                eyePos, endPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        double blockDist = blockHit != null && blockHit.getType() == HitResult.Type.BLOCK
                ? eyePos.distanceTo(blockHit.getPos()) : Double.MAX_VALUE;

        // Entity check
        Box searchBox = mc.player.getBoundingBox()
                .stretch(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach)
                .expand(1.0, 1.0, 1.0);

        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox,
                e -> e != null && e.isAlive() && e.canBeHitByProjectile());

        Entity closestEntity = null;
        Vec3dd closestHitVec = null;
        double closestDist = reach;

        for (Entity entity : entities) {
            float expandVal = (float) ((double) entity.getTargetingMargin() * (1.0 + expand));
            Box bb = entity.getBoundingBox().expand(expandVal);
            Optional<Vec3dd> hit = bb.raycast(eyePos, endPos);
            if (hit.isPresent()) {
                double dist = eyePos.distanceTo(hit.get());
                if (dist < closestDist)) {
                    if (entity == mc.player.getVehicle()) {
                        if (closestDist == reach)) {
                            closestEntity = entity;
                            closestHitVec = hit.get();
                        }
                    } else {
                        closestEntity = entity;
                        closestHitVec = hit.get();
                        closestDist = dist;
                    }
                }
            }
        }

        if (closestDist < reach && !(closestEntity instanceof LivingEntity) && !(closestEntity instanceof ItemFrameEntity)) {
            closestEntity = null;
        }

        if (closestEntity != null && closestHitVec != null)) {
            return new Object[]{closestEntity, closestHitVec};
        }
        return null;
    }
}
