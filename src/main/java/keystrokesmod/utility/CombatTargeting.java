package keystrokesmod.utility;

import keystrokesmod.module.impl.world.AntiBot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;

public final class CombatTargeting implements IMinecraftInstance {
    private CombatTargeting() {
    }

    public static PlayerEntity findTarget(double maxDistanceSq) {
        return findTarget(maxDistanceSq, true);
    }

    public static PlayerEntity findTarget(double maxDistanceSq, boolean ignoreTeamates) {
        PlayerEntity mouseOverTarget = getMouseOverTarget(maxDistanceSq, ignoreTeamates);
        if (mouseOverTarget != null) {
            return mouseOverTarget;
        }

        return findClosestTarget(maxDistanceSq, ignoreTeamates);
    }

    public static PlayerEntity findClosestTarget(double maxDistanceSq) {
        return findClosestTarget(maxDistanceSq, true);
    }

    public static PlayerEntity findClosestTarget(double maxDistanceSq, boolean ignoreTeamates) {
        if (mc == null || mc.world == null) {
            return null;
        }

        PlayerEntity closest = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isValidPlayer(player, maxDistanceSq, ignoreTeamates)) {
                continue;
            }

            double distanceSq = RotationUtils.distanceSqFromEyeToClosestOnAABB(player);
            if (distanceSq < closestDistanceSq) {
                closestDistanceSq = distanceSq;
                closest = player;
            }
        }

        return closest;
    }

    public static PlayerEntity getMouseOverTarget(double maxDistanceSq) {
        return getMouseOverTarget(maxDistanceSq, true);
    }

    public static PlayerEntity getMouseOverTarget(double maxDistanceSq, boolean ignoreTeamates) {
        if (mc == null || mc.crosshairTargetr == null) {
            return null;
        }

        HitResult objectMouseOver = mc.crosshairTargetr;
        return asValidPlayer(objectMouseOver.entityHit, maxDistanceSq, ignoreTeamates);
    }

    public static PlayerEntity asValidPlayer(Entity entity, double maxDistanceSq) {
        return asValidPlayer(entity, maxDistanceSq, true);
    }

    public static PlayerEntity asValidPlayer(Entity entity, double maxDistanceSq, boolean ignoreTeamates) {
        if (!(entity instanceof PlayerEntity)) {
            return null;
        }

        PlayerEntity player = (PlayerEntity) entity;
        return isValidPlayer(player, maxDistanceSq, ignoreTeamates) ? player : null;
    }

    public static boolean isValidPlayer(PlayerEntity player, double maxDistanceSq) {
        return isValidPlayer(player, maxDistanceSq, true);
    }

    public static boolean isValidPlayer(PlayerEntity player, double maxDistanceSq, boolean ignoreTeamates) {
        return isTrackablePlayer(player, ignoreTeamates) && isWithinRange(player, maxDistanceSq);
    }

    public static boolean isTrackablePlayer(PlayerEntity player) {
        return isTrackablePlayer(player, true);
    }

    public static boolean isTrackablePlayer(PlayerEntity player, boolean ignoreTeamates) {
        if (!Utils.nullCheck() || player == null || player == mc.player || player.isDead || player.deathTime != 0) {
            return false;
        }

        if (Utils.isFriended(player) || AntiBot.isBot(player)) {
            return false;
        }

        if (ignoreTeamates && Utils.isTeamate(player)) {
            return false;
        }

        return true;
    }

    public static boolean isWithinRange(PlayerEntity player, double maxDistanceSq) {
        if (player == null) {
            return false;
        }

        return RotationUtils.distanceSqFromEyeToClosestOnAABB(player) <= maxDistanceSq;
    }
}
