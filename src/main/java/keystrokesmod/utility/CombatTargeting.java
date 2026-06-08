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

    public static PlayerEntity findTarget(double maxDistanceSq, boolean ignoreTeammates) {
        PlayerEntity mouseOverTarget = getMouseOverTarget(maxDistanceSq, ignoreTeammates);
        if (mouseOverTarget != null) {
            return mouseOverTarget;
        }

        return findClosestTarget(maxDistanceSq, ignoreTeammates);
    }

    public static PlayerEntity findClosestTarget(double maxDistanceSq) {
        return findClosestTarget(maxDistanceSq, true);
    }

    public static PlayerEntity findClosestTarget(double maxDistanceSq, boolean ignoreTeammates) {
        if (mc == null || mc.world == null) {
            return null;
        }

        PlayerEntity closest = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isValidPlayer(player, maxDistanceSq, ignoreTeammates)) {
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

    public static PlayerEntity getMouseOverTarget(double maxDistanceSq, boolean ignoreTeammates) {
        if (mc == null || mc.crosshairTargetr == null) {
            return null;
        }

        HitResult objectMouseOver = mc.crosshairTargetr;
        return asValidPlayer(objectMouseOver.entityHit, maxDistanceSq, ignoreTeammates);
    }

    public static PlayerEntity asValidPlayer(Entity entity, double maxDistanceSq) {
        return asValidPlayer(entity, maxDistanceSq, true);
    }

    public static PlayerEntity asValidPlayer(Entity entity, double maxDistanceSq, boolean ignoreTeammates) {
        if (!(entity instanceof PlayerEntity)) {
            return null;
        }

        PlayerEntity player = (PlayerEntity) entity;
        return isValidPlayer(player, maxDistanceSq, ignoreTeammates) ? player : null;
    }

    public static boolean isValidPlayer(PlayerEntity player, double maxDistanceSq) {
        return isValidPlayer(player, maxDistanceSq, true);
    }

    public static boolean isValidPlayer(PlayerEntity player, double maxDistanceSq, boolean ignoreTeammates) {
        return isTrackablePlayer(player, ignoreTeammates) && isWithinRange(player, maxDistanceSq);
    }

    public static boolean isTrackablePlayer(PlayerEntity player) {
        return isTrackablePlayer(player, true);
    }

    public static boolean isTrackablePlayer(PlayerEntity player, boolean ignoreTeammates) {
        if (!Utils.nullCheck() || player == null || player == mc.player || player.isDead || player.deathTime != 0) {
            return false;
        }

        if (Utils.isFriended(player) || AntiBot.isBot(player)) {
            return false;
        }

        if (ignoreTeammates && Utils.isTeammate(player)) {
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
