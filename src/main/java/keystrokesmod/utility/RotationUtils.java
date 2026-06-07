package keystrokesmod.utility;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

import com.google.common.base.Predicates;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.helper.RotationHelper;
import keystrokesmod.module.impl.client.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RotationUtils implements IMinecraftInstance {
    public static float renderPitch;
    public static float prevRenderPitch;
    public static float renderYaw;
    public static float prevRenderYaw;
    public static float[] serverRotations = new float[] { 0, 0 } ;

    public static Float[] fakeRotations;
    public static boolean setFakeRotations;

    public static void setFakeRotations(float yaw, float pitch) {
        fakeRotations = new Float[] { yaw, pitch };
        setFakeRotations = true;
    }

    public static void setRenderYaw(float yaw) {
        mc.player.yawHead = yaw;
        if (Settings.rotateBody.isToggled() && Settings.fullBody.isToggled()) {
            mc.player.prevRenderYawOffset = prevRenderYaw;
            mc.player.bodyYaw = yaw;
        }
    }

    public static float[] getRotations(BlockPos blockPos, final float n, final float n2) {
        final float[] array = getRotations(blockPos);
        return fixRotation(array[0], array[1], n, n2);
    }

    public static float[] getRotationsToBlock(BlockPos blockPos, Direction facing, final float yaw, final float pitch) {
        final float[] array = getRotationsToBlock(blockPos, facing);
        return fixRotation(array[0], array[1], yaw, pitch);
    }

    public static float[] getRotations(BlockPos blockPos) {
        double x = blockPos.getX() + 0.45 - mc.player.getX();
        double y = blockPos.getY() + 0.45 - (mc.player.getY() + mc.player.getEyeHeight());
        double z = blockPos.getZ() + 0.45 - mc.player.getZ();

        float angleToBlock = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0f;
        float deltaYaw = MathHelper.wrapAngleTo180_float(angleToBlock - mc.player.getYaw());
        float yaw = mc.player.getYaw() + deltaYaw;

        double distance = MathHelper.sqrt_double(x * x + z * z);
        float angleToBlockPitch = (float) (-(Math.atan2(y, distance) * (180 / Math.PI)));
        float deltaPitch = MathHelper.wrapAngleTo180_float(angleToBlockPitch - mc.player.getPitch());
        float pitch = mc.player.getPitch() + deltaPitch;

        pitch = clampPitch(pitch);

        return new float[] { yaw, pitch };
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX + 1.0 - mc.player.getX();
        double y = posY + 1.0 - (mc.player.getY() + mc.player.getEyeHeight());
        double z = posZ + 1.0 - mc.player.getZ();

        float angleToBlock = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0f;
        float deltaYaw = MathHelper.wrapAngleTo180_float(angleToBlock - mc.player.getYaw());
        float yaw = mc.player.getYaw() + deltaYaw;

        double distance = MathHelper.sqrt_double(x * x + z * z);
        float angleToBlockPitch = (float) (-(Math.atan2(y, distance) * (180 / Math.PI)));
        float deltaPitch = MathHelper.wrapAngleTo180_float(angleToBlockPitch - mc.player.getPitch());
        float pitch = mc.player.getPitch() + deltaPitch;

        pitch = clampPitch(pitch);

        return new float[] { yaw, pitch };
    }

    public static float[] getRotations(Vec3d vec3) {
        double x = vec3.x + 1.0D - mc.player.getX();
        double y = vec3.y + 1.0D - (mc.player.getY() + mc.player.getEyeHeight());
        double z = vec3.z + 1.0D - mc.player.getZ();

        float angleToBlock = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0f;
        float deltaYaw = MathHelper.wrapAngleTo180_float(angleToBlock - mc.player.getYaw());
        float yaw = mc.player.getYaw() + deltaYaw;

        double distance = MathHelper.sqrt_double(x * x + z * z);
        float angleToBlockPitch = (float) (-(Math.atan2(y, distance) * (180 / Math.PI)));
        float deltaPitch = MathHelper.wrapAngleTo180_float(angleToBlockPitch - mc.player.getPitch());
        float pitch = mc.player.getPitch() + deltaPitch;

        pitch = clampPitch(pitch);

        return new float[] { yaw, pitch };
    }

    public static float[] getRotations(Entity entity, final float yaw, final float pitch) {
        final float[] array = getRotations(entity);
        if (array == null) {
            return null;
        }
        return fixRotation(array[0], array[1], yaw, pitch);
    }

    public static float[] getRotationsToBlock(final BlockPos pos, final Direction facing) {
        double diffX = pos.getX() + 0.45 - mc.player.getX();
        double diffY = pos.getY() + 0.45 - (mc.player.getY() + mc.player.getEyeHeight());
        double diffZ = pos.getZ() + 0.45 - mc.player.getZ();
        if (facing != null) {
            diffX += facing.getDirectionVec().getX() * 0.5;
            diffY += facing.getDirectionVec().getY() * 0.5;
            diffZ += facing.getDirectionVec().getZ() * 0.5;
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)(Math.atan2(diffZ, diffX) * 57.295780181884766) - 90.0f;
        final float pitch = (float)(-(Math.atan2(diffY, dist) * 57.295780181884766));
        return new float[] { mc.player.getYaw() + MathHelper.wrapAngleTo180_float(yaw - mc.player.getYaw()), clampPitch(mc.player.getPitch() + MathHelper.wrapAngleTo180_float(pitch - mc.player.getPitch())) };
    }

    public static double distanceFromYaw(final Entity entity, final boolean b) {
        return Math.abs(MathHelper.wrapAngleTo180_double(i(entity.posX, entity.posZ) - ((b && PreMotionEvent.setRenderYaw()) ? RotationUtils.renderYaw : mc.player.getYaw())));
    }

    public static float i(final double n, final double n2) {
        return (float)(Math.atan2(n - mc.player.getX(), n2 - mc.player.getZ()) * 57.295780181884766 * -1.0);
    }

    public static boolean isPossibleToHit(Entity target, double reach, float[] rotations) {
        final Vec3d eyePosition = mc.player.getPositionEyes(1.0f);

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float radianYaw = -yaw * 0.017453292f - (float)Math.PI;
        final float radianPitch = -pitch * 0.017453292f;

        final float cosYaw = MathHelper.cos(radianYaw);
        final float sinYaw = MathHelper.sin(radianYaw);
        final float cosPitch = -MathHelper.cos(radianPitch);
        final float sinPitch = MathHelper.sin(radianPitch);

        final Vec3d lookVector = new Vec3d(
                sinYaw * cosPitch, // x
                sinPitch,         // y
                cosYaw * cosPitch // z
        );

        final double lookVecX = lookVector.x * reach;
        final double lookVecY = lookVector.y * reach;
        final double lookVecZ = lookVector.z * reach;

        final Vec3d endPosition = eyePosition.addVector(lookVecX, lookVecY, lookVecZ);

        final Entity renderViewEntity = mc.getCameraEntity();
        final Box expandedBox = renderViewEntity
                .getBoundingBox()
                .addCoord(lookVecX, lookVecY, lookVecZ)
                .expand(1.0, 1.0, 1.0);

        final List<Entity> entitiesInPath = mc.world.getEntitiesWithinAABBExcludingEntity(renderViewEntity, expandedBox);
        for (Entity entity : entitiesInPath) {
            if (entity == target && entity.canBeCollidedWith()) {
                final float borderSize = entity.getCollisionBorderSize();
                final Box entityBox = entity.getBoundingBox()
                        .expand(borderSize, borderSize, borderSize);
                final HitResult intercept = entityBox.calculateIntercept(eyePosition, endPosition);
                return intercept != null;
            }
        }

        return false;
    }

    public static boolean inRange(final BlockPos blockPos, final double n) {
        final float[] array = RotationUtils.getRotations(blockPos);
        final Vec3d getPositionEyes = mc.player.getPositionEyes(1.0f);
        final float n2 = -array[0] * 0.017453292f;
        final float n3 = -array[1] * 0.017453292f;
        final float cos = MathHelper.cos(n2 - 3.1415927f);
        final float sin = MathHelper.sin(n2 - 3.1415927f);
        final float n4 = -MathHelper.cos(n3);
        final Vec3d vec3 = new Vec3d(sin * n4, MathHelper.sin(n3), cos * n4);
        Block block = BlockUtils.getBlock(blockPos);
        BlockState blockState = BlockUtils.getBlockState(blockPos);
        if (block != null && blockState != null) {
            Box boundingBox = block.getCollisionBoundingBox(mc.world, blockPos, blockState);
            if (boundingBox != null) {
                Vec3d targetVec = getPositionEyes.addVector(vec3.x * n, vec3.y * n, vec3.z * n);
                HitResult intercept = boundingBox.calculateIntercept(getPositionEyes, targetVec);
                if (intercept != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float[] getRotations(final Entity entity) {
        return getRotations(entity, PLAYER_OFFSETS.NONE);
    }

    public static float[] getRotations(final Entity entity, PLAYER_OFFSETS playerOffset) {
        if (entity == null) {
            return null;
        }
        double deltaX = entity.posX - mc.player.getX();
        double deltaZ = entity.posZ - mc.player.getZ();
        double deltaY;
        if (entity instanceof LivingEntity) {
            final LivingEntity entityLivingBase = (LivingEntity) entity;
            deltaY = entityLivingBase.posY + playerOffset.getHeightOffset(entityLivingBase) * 0.9 - (mc.player.getY() + mc.player.getEyeHeight());
        }
        else {
            deltaY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0 - (mc.player.getY() + mc.player.getEyeHeight());
        }
        return new float[] { mc.player.getYaw() + MathHelper.wrapAngleTo180_float((float) (Math.atan2(deltaZ, deltaX) * 57.295780181884766) - 90.0f - mc.player.getYaw()), clampPitch(mc.player.getPitch() + MathHelper.wrapAngleTo180_float((float) (-(Math.atan2(deltaY, MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ)) * 57.295780181884766)) - mc.player.getPitch()) + 3.0f)};
    }

    public static float[] getRotationsToPoint(double x, double y, double z) {
        return getRotationsToPoint(x, y, z, mc.player.getYaw(), mc.player.getPitch());
    }

    /**
     * Base-aware overload for silent rotation paths. When the target is directly above/below
     * (horizDist near zero), preserves baseYaw to avoid atan2(0,0) -> -90 degenerate yaw.
     */
    public static float[] getRotationsToPoint(double x, double y, double z, float baseYaw, float basePitch) {
        double deltaX = x - mc.player.getX();
        double deltaZ = z - mc.player.getZ();
        double deltaY = y - (mc.player.getY() + mc.player.getEyeHeight());
        double horizDistSq = deltaX * deltaX + deltaZ * deltaZ;

        float yaw;
        float targetPitch;
        if (horizDistSq < 1.0E-12) {
            yaw = baseYaw;
            targetPitch = (float) (-(Math.atan2(deltaY, 0) * 57.295780181884766));
        } else {
            float targetYaw = (float) (Math.atan2(deltaZ, deltaX) * 57.295780181884766) - 90.0f;
            yaw = baseYaw + MathHelper.wrapAngleTo180_float(targetYaw - baseYaw);
            double horizDist = MathHelper.sqrt_double(horizDistSq);
            targetPitch = (float) (-(Math.atan2(deltaY, horizDist) * 57.295780181884766));
        }

        float pitch = basePitch + MathHelper.wrapAngleTo180_float(targetPitch - basePitch) + 3.0f;
        return new float[] { yaw, clampPitch(pitch) };
    }

    public static float[] getRotations(final Entity entity, double horizontalMultipoint, double verticalMultipoint, final float baseYaw, final float basePitch) {
        Vec3d aimPoint = getAimPoint(entity, horizontalMultipoint, verticalMultipoint);
        if (aimPoint == null) {
            return null;
        }
        return getRotationsToPoint(aimPoint.x, aimPoint.y, aimPoint.z, baseYaw, basePitch);
    }

    /**
     * Returns the aim point Vec3d for the given entity and multipoint settings.
     * Extracted from getRotations logic for backup-point fallback.
     */
    public static Vec3d getAimPoint(Entity entity, double horizontalMultipoint, double verticalMultipoint) {
        if (entity == null || mc.player == null) return null;
        float borderSize = entity.getCollisionBorderSize();
        Box bb = entity.getBoundingBox().expand(borderSize, borderSize, borderSize);
        double centerX = (bb.minX + bb.maxX) / 2.0;
        double centerY;
        if (entity instanceof LivingEntity) {
            centerY = entity.posY + ((LivingEntity) entity).getEyeHeight();
        } else {
            centerY = (bb.minY + bb.maxY) / 2.0;
        }
        double centerZ = (bb.minZ + bb.maxZ) / 2.0;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        if (bb.isVecInside(eye)) {
            return new Vec3d(centerX, eye.y, centerZ);
        }
        Vec3d cl = closestPointOnAabb(bb, eye);
        double tH = Math.max(0.0, Math.min(1.0, horizontalMultipoint / 100.0));
        double tV = Math.max(0.0, Math.min(1.0, verticalMultipoint / 100.0));
        double targetX = centerX + (cl.x - centerX) * tH;
        double targetY = centerY + (cl.y - centerY) * tV;
        double targetZ = centerZ + (cl.z - centerZ) * tH;
        return new Vec3d(targetX, targetY, targetZ);
    }

    public static Vec3d closestPointOnAabb(Box box, Vec3d point) {
        double x = Math.max(box.minX, Math.min(box.maxX, point.x));
        double y = Math.max(box.minY, Math.min(box.maxY, point.y));
        double z = Math.max(box.minZ, Math.min(box.maxZ, point.z));
        return new Vec3d(x, y, z);
    }

    private static final double BACKUP_FACE_INSET = 0.05;
    private static final int BACKUP_TARGET_TOTAL = 30;

    public static List<Vec3d> buildBackupPoints(Entity entity, Vec3d eye) {
        if (entity == null || mc.player == null) return new ArrayList<>();
        float borderSize = entity.getCollisionBorderSize();
        Box bb = entity.getBoundingBox().expand(borderSize, borderSize, borderSize);

        double sizeX = bb.maxX - bb.minX;
        double sizeY = bb.maxY - bb.minY;
        double sizeZ = bb.maxZ - bb.minZ;

        boolean xPos = eye.x > bb.maxX;
        boolean xNeg = eye.x < bb.minX;
        boolean yPos = eye.y > bb.maxY;
        boolean yNeg = eye.y < bb.minY;
        boolean zPos = eye.z > bb.maxZ;
        boolean zNeg = eye.z < bb.minZ;

        int visibleFaceCount = (xPos || xNeg ? 1 : 0) + (yPos || yNeg ? 1 : 0) + (zPos || zNeg ? 1 : 0);
        if (visibleFaceCount == 0) return new ArrayList<>();

        int pointsPerFace = BACKUP_TARGET_TOTAL / visibleFaceCount;
        List<Vec3d> points = new ArrayList<>(BACKUP_TARGET_TOTAL + 6);

        if (xPos || xNeg) {
            double fixedX = xPos ? bb.maxX - BACKUP_FACE_INSET : bb.minX + BACKUP_FACE_INSET;
            addFaceGrid(points, 0, fixedX,
                    bb.minY + BACKUP_FACE_INSET, bb.maxY - BACKUP_FACE_INSET,
                    bb.minZ + BACKUP_FACE_INSET, bb.maxZ - BACKUP_FACE_INSET,
                    pointsPerFace, sizeY, sizeZ);
        }

        if (yPos || yNeg) {
            double fixedY = yPos ? bb.maxY - BACKUP_FACE_INSET : bb.minY + BACKUP_FACE_INSET;
            addFaceGrid(points, 1, fixedY,
                    bb.minX + BACKUP_FACE_INSET, bb.maxX - BACKUP_FACE_INSET,
                    bb.minZ + BACKUP_FACE_INSET, bb.maxZ - BACKUP_FACE_INSET,
                    pointsPerFace, sizeX, sizeZ);
        }

        if (zPos || zNeg) {
            double fixedZ = zPos ? bb.maxZ - BACKUP_FACE_INSET : bb.minZ + BACKUP_FACE_INSET;
            addFaceGrid(points, 2, fixedZ,
                    bb.minX + BACKUP_FACE_INSET, bb.maxX - BACKUP_FACE_INSET,
                    bb.minY + BACKUP_FACE_INSET, bb.maxY - BACKUP_FACE_INSET,
                    pointsPerFace, sizeX, sizeY);
        }

        return points;
    }

    private static void addFaceGrid(List<Vec3d> out, int fixedAxis, double fixedVal,
                                     double uMin, double uMax, double vMin, double vMax,
                                     int targetPoints, double dimU, double dimV) {
        if (dimU < 1e-4 || dimV < 1e-4) {
            double uMid = (uMin + uMax) / 2.0;
            double vMid = (vMin + vMax) / 2.0;
            switch (fixedAxis) {
                case 0: out.add(new Vec3d(fixedVal, uMid, vMid)); break;
                case 1: out.add(new Vec3d(uMid, fixedVal, vMid)); break;
                case 2: out.add(new Vec3d(uMid, vMid, fixedVal)); break;
            }
            return;
        }

        double ratio = dimU / dimV;
        int gridU = Math.max(2, (int) Math.round(Math.sqrt(targetPoints * ratio)));
        int gridV = Math.max(2, (int) Math.round(Math.sqrt(targetPoints / ratio)));

        for (int i = 0; i < gridU; i++) {
            double u = uMin + (uMax - uMin) * i / (gridU - 1);
            for (int j = 0; j < gridV; j++) {
                double v = vMin + (vMax - vMin) * j / (gridV - 1);
                switch (fixedAxis) {
                    case 0: out.add(new Vec3d(fixedVal, u, v)); break;
                    case 1: out.add(new Vec3d(u, fixedVal, v)); break;
                    case 2: out.add(new Vec3d(u, v, fixedVal)); break;
                }
            }
        }
    }

    /**
     * Returns squared distance from player eye to the closest point on the entity's expanded AABB.
     */
    public static double distanceSqFromEyeToClosestOnAABB(Entity entity) {
        if (entity == null || mc.player == null) return Double.MAX_VALUE;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        float borderSize = entity.getCollisionBorderSize();
        Box bb = entity.getBoundingBox().expand(borderSize, borderSize, borderSize);
        Vec3d closest = closestPointOnAabb(bb, eye);
        double dx = eye.x - closest.x;
        double dy = eye.y - closest.y;
        double dz = eye.z - closest.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Returns distance from player eye to the closest point on the entity's expanded AABB.
     */
    public static double distanceFromEyeToClosestOnAABB(Entity entity) {
        double dSq = distanceSqFromEyeToClosestOnAABB(entity);
        return dSq == Double.MAX_VALUE ? Double.MAX_VALUE : Math.sqrt(dSq);
    }

    public static boolean canAimAtPoint(Vec3d eye, Vec3d point, Entity target, double range) {
        return canAimAtPoint(eye, point, target, range, false, true);
    }

    public static boolean canAimAtPoint(Vec3d eye, Vec3d point, Entity target, double range, boolean allowThroughBlocks, boolean allowThroughEntities) {
        if (target == null) return false;
        double dx = point.x - eye.x;
        double dy = point.y - eye.y;
        double dz = point.z - eye.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6) return false;
        double scale = range / len;
        Vec3d end = new Vec3d(eye.x + dx * scale, eye.y + dy * scale, eye.z + dz * scale);

        float borderSize = target.getCollisionBorderSize();
        Box aabb = target.getBoundingBox().expand(borderSize, borderSize, borderSize);
        HitResult entityHit = aabb.calculateIntercept(eye, end);
        if (entityHit == null) return false;

        double entityDistSq = eye.squareDistanceTo(entityHit.hitVec);
        if (!allowThroughBlocks) {
            HitResult blockHit = mc.world.rayTraceBlocks(eye, end, false, false, false);
            if (blockHit != null && blockHit.typeOfHit == HitResult.MovingObjectType.BLOCK) {
                double blockDistSq = eye.squareDistanceTo(blockHit.hitVec);
                if (blockDistSq < entityDistSq) return false;
            }
        }
        if (!allowThroughEntities && hasEntityBlockingPath(eye, end, target, entityDistSq)) {
            return false;
        }
        return true;
    }

    private static boolean hasEntityBlockingPath(Vec3d eye, Vec3d end, Entity target, double targetDistSq) {
        if (mc.player == null || mc.world == null) return false;
        Vec3d delta = end.subtract(eye);
        Box searchBox = mc.player.getBoundingBox()
                .addCoord(delta.x, delta.y, delta.z)
                .expand(1.0, 1.0, 1.0);
        List<Entity> entities = mc.world.getEntitiesInAABBexcluding(mc.player, searchBox, Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        for (Entity entity : entities) {
            if (entity == null || entity == target || entity.isRemoved()) {
                continue;
            }
            float border = entity.getCollisionBorderSize();
            Box bb = entity.getBoundingBox().expand(border, border, border);
            HitResult hit = bb.calculateIntercept(eye, end);
            if (bb.isVecInside(eye)) {
                return true;
            }
            if (hit != null) {
                double entityDistSq = eye.squareDistanceTo(hit.hitVec);
                if (entityDistSq < targetDistSq - 1.0E-7) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPathBlockedByEntity(Vec3d eye, Vec3d hitVec, Entity target) {
        if (eye == null || hitVec == null || target == null) return false;
        double targetDistSq = eye.squareDistanceTo(hitVec);
        return hasEntityBlockingPath(eye, hitVec, target, targetDistSq);
    }

    public static boolean isEyeInsideEntityAABB(Entity entity) {
        if (entity == null || mc.player == null) return false;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        float borderSize = entity.getCollisionBorderSize();
        Box bb = entity.getBoundingBox().expand(borderSize, borderSize, borderSize);
        return bb.isVecInside(eye);
    }

    private static boolean mainRayHitsTargetAABB(Vec3d eye, Vec3d point, Entity target, double range) {
        double dx = point.x - eye.x;
        double dy = point.y - eye.y;
        double dz = point.z - eye.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6) return false;
        double scale = range / len;
        Vec3d end = new Vec3d(eye.x + dx * scale, eye.y + dy * scale, eye.z + dz * scale);
        float borderSize = target.getCollisionBorderSize();
        Box aabb = target.getBoundingBox().expand(borderSize, borderSize, borderSize);
        return aabb.calculateIntercept(eye, end) != null;
    }

    public static boolean hasValidAimPoint(Entity entity, double hMult, double vMult, double range) {
        return hasValidAimPoint(entity, hMult, vMult, range, false, true);
    }

    public static boolean hasValidAimPoint(Entity entity, double hMult, double vMult, double range, boolean allowThroughBlocks, boolean allowThroughEntities) {
        if (entity == null || mc.player == null) return false;
        Vec3d mainPoint = getAimPoint(entity, hMult, vMult);
        if (mainPoint == null) return false;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        if (eye.squareDistanceTo(mainPoint) < 1e-6) return true;

        if (!mainRayHitsTargetAABB(eye, mainPoint, entity, range)) {
            return false;
        }

        if (canAimAtPoint(eye, mainPoint, entity, range, allowThroughBlocks, allowThroughEntities)) {
            return true;
        }

        List<Vec3d> backups = buildBackupPoints(entity, eye);
        Collections.sort(backups, Comparator.comparingDouble(p -> {
            double dx = p.x - eye.x;
            double dy = p.y - eye.y;
            double dz = p.z - eye.z;
            return dx * dx + dy * dy + dz * dz;
        }));
        for (Vec3d p : backups) {
            if (canAimAtPoint(eye, p, entity, range, allowThroughBlocks, allowThroughEntities)) {
                return true;
            }
        }
        return false;
    }

    public static float[] getRotationsWithBackup(Entity entity, double horizontalMultipoint, double verticalMultipoint, float baseYaw, float basePitch, double range) {
        return getRotationsWithBackup(entity, horizontalMultipoint, verticalMultipoint, baseYaw, basePitch, range, false, true);
    }

    public static float[] getRotationsWithBackup(Entity entity, double horizontalMultipoint, double verticalMultipoint, float baseYaw, float basePitch, double range, boolean allowThroughBlocks, boolean allowThroughEntities) {
        if (entity == null || mc.player == null) return null;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        float borderSize = entity.getCollisionBorderSize();
        Box bb = entity.getBoundingBox().expand(borderSize, borderSize, borderSize);
        if (bb.isVecInside(eye)) {
            double centerX = (bb.minX + bb.maxX) / 2.0;
            double centerZ = (bb.minZ + bb.maxZ) / 2.0;
            return getRotationsToPoint(centerX, eye.y, centerZ, baseYaw, basePitch);
        }
        Vec3d mainPoint = getAimPoint(entity, horizontalMultipoint, verticalMultipoint);
        if (mainPoint == null) return null;
        if (eye.squareDistanceTo(mainPoint) < 1e-6) return null;

        if (!mainRayHitsTargetAABB(eye, mainPoint, entity, range)) {
            return getRotationsToPoint(mainPoint.x, mainPoint.y, mainPoint.z, baseYaw, basePitch);
        }

        if (canAimAtPoint(eye, mainPoint, entity, range, allowThroughBlocks, allowThroughEntities)) {
            return getRotationsToPoint(mainPoint.x, mainPoint.y, mainPoint.z, baseYaw, basePitch);
        }

        List<Vec3d> backups = buildBackupPoints(entity, eye);
        Collections.sort(backups, Comparator.comparingDouble(p -> {
            double dx = p.x - eye.x;
            double dy = p.y - eye.y;
            double dz = p.z - eye.z;
            return dx * dx + dy * dy + dz * dz;
        }));

        for (Vec3d p : backups) {
            if (canAimAtPoint(eye, p, entity, range, allowThroughBlocks, allowThroughEntities)) {
                return getRotationsToPoint(p.x, p.y, p.z, baseYaw, basePitch);
            }
        }
        return null;
    }

    public static float[] getRotationsPredicated(final Entity entity, final int ticks) {
        if (entity == null) {
            return null;
        }
        if (ticks == 0) {
            return getRotations(entity);
        }
        double posX = entity.posX;
        final double posY = entity.posY;
        double posZ = entity.posZ;
        final double n2 = posX - entity.lastTickPosX;
        final double n3 = posZ - entity.lastTickPosZ;
        for (int i = 0; i < ticks; ++i) {
            posX += n2;
            posZ += n3;
        }
        final double n4 = posX - mc.player.getX();
        double n5;
        if (entity instanceof LivingEntity) {
            n5 = posY + entity.getEyeHeight() * 0.9 - (mc.player.getY() + mc.player.getEyeHeight());
        }
        else {
            n5 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0 - (mc.player.getY() + mc.player.getEyeHeight());
        }
        final double n6 = posZ - mc.player.getZ();
        return new float[] { applyVanilla(mc.player.getYaw() + MathHelper.wrapAngleTo180_float((float)(Math.atan2(n6, n4) * 57.295780181884766) - 90.0f - mc.player.getYaw())), clampPitch(mc.player.getPitch() + MathHelper.wrapAngleTo180_float((float)(-(Math.atan2(n5, MathHelper.sqrt_double(n4 * n4 + n6 * n6)) * 57.295780181884766)) - mc.player.getPitch()) + 3.0f) };
    }

    private static final float FAR_THRESHOLD = 180f;

    /**
     * Smoothly interpolates from base to target rotation using linear step model.
     * Steps along the combined (yaw, pitch) direction so both axes move together proportionally,
     * simulating human mouse movement (one fluid motion).
     * @param speed 0 = no movement, 30 = practically instant
     */
    public static float[] smoothRotation(float baseYaw, float basePitch,
                                          float targetYaw, float targetPitch,
                                          int speed) {
        return smoothRotation(baseYaw, basePitch, targetYaw, targetPitch, speed, 0f);
    }

    /**
     * Overload with configurable randomization (0-100%). Higher randomization varies step size
     * per tick to bypass anticheat pattern analysis (consistent deltas, constant acceleration).
     * @param speed 0 = no movement, 30 = practically instant
     */
    public static float[] smoothRotation(float baseYaw, float basePitch,
                                          float targetYaw, float targetPitch,
                                          int speed, float randomizationPercent) {
        if (speed <= 0) {
            return new float[] { baseYaw, clampPitch(basePitch) };
        }
        if (speed >= 30) {
            return new float[] { targetYaw, clampPitch(targetPitch) };
        }
        float deltaYaw = MathHelper.wrapAngleTo180_float(targetYaw - baseYaw);
        float deltaPitch = targetPitch - basePitch;
        float magnitude = (float) MathHelper.sqrt_double(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
        if (magnitude < 0.001f) {
            return new float[] { targetYaw, clampPitch(targetPitch) };
        }
        float t = speed / 30f;
        float stepSize = t * t * 180f;
        float range = 0.6f * (float)(randomizationPercent / 100.0);
        float multiplier = (range <= 0.001f) ? 1.0f : (1.0f - range/2f + (float)(Math.random() * range));
        stepSize *= multiplier;
        float proximityFactor = Math.min(1f, magnitude / FAR_THRESHOLD);
        proximityFactor = (float) Math.pow(proximityFactor, 0.7);
        float maxSlowdown = (float)(randomizationPercent / 100.0);
        // Cap proximity slowdown at 20% (min 80% speed) so high randomization doesn't kill aim assist
        float proximityMult = Math.max(0.8f, 1.0f - maxSlowdown * (1.0f - proximityFactor));
        stepSize *= proximityMult;
        float stepLength = Math.min(stepSize, magnitude);
        float scale = stepLength / magnitude;
        float stepYaw = deltaYaw * scale;
        float stepPitch = deltaPitch * scale;
        float yaw = baseYaw + stepYaw;
        float pitch = basePitch + stepPitch;
        return new float[] { yaw, clampPitch(pitch) };
    }

    public static float clampPitch(final float n) {
        return MathHelper.clamp_float(n, -90.0f, 90.0f);
    }

    // TODO remove calls to this from the util as it's done globally in RotationHelper
    public static float[] fixRotation(float targetYaw, float targetPitch, final float yaw, final float pitch) {
        targetYaw = RotationHelper.unwrapYaw(targetYaw, yaw);
        float n5 = targetYaw - yaw;
        final float abs = Math.abs(n5);
        final float n7 = targetPitch - pitch;
        final float n8 = mc.options.mouseSensitivity * 0.6f + 0.2f;
        final double n9 = n8 * n8 * n8 * 1.2;
        final float n10 = (float) (Math.round((double) n5 / n9) * n9);
        final float n11 = (float) (Math.round((double) n7 / n9) * n9);
        targetYaw = yaw + n10;
        targetPitch = pitch + n11;
        return new float[] { targetYaw, clampPitch(targetPitch) };
    }

    public static float angle(final double n, final double n2) {
        return (float) (Math.atan2(n - mc.player.getX(), n2 - mc.player.getZ()) * 57.295780181884766 * -1.0);
    }

    public static float deltaAngle(final double n, final double n2) {
        return (float) (Math.atan2(n, n2) * 57.295780181884766 * -1.0);
    }

    public static HitResult rayCast(double distance, float yaw, float pitch, boolean collisionCheck) {
        final Vec3d getPositionEyes = mc.player.getPositionEyes(1.0f);
        final float n4 = -yaw * 0.017453292f;
        final float n5 = -pitch * 0.017453292f;
        final float cos = MathHelper.cos(n4 - 3.1415927f);
        final float sin = MathHelper.sin(n4 - 3.1415927f);
        final float n6 = -MathHelper.cos(n5);
        final Vec3d vec3 = new Vec3d(sin * n6, MathHelper.sin(n5), cos * n6);
        return mc.world.rayTraceBlocks(getPositionEyes, getPositionEyes.addVector(vec3.x * distance, vec3.y * distance, vec3.z * distance), true, collisionCheck, true);
    }

    public static HitResult rayCastBlock(final double distance, final float yaw, final float pitch) {
        Vec3d eyeVec = mc.player.getPositionEyes(1.0f);
        Vec3d lookVec = Utils.getLookVec(yaw, pitch);
        Vec3d sumVec = eyeVec.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        HitResult mop = mc.world.rayTraceBlocks(eyeVec, sumVec, false, false, false);
        if (mop == null || mop.typeOfHit != HitResult.MovingObjectType.BLOCK) {
            return null;
        }
        return mop;
    }

    public static HitResult rayTraceCustom(double blockReachDistance, float yaw, float pitch) {
        final Vec3d vec3 = mc.player.getPositionEyes(1.0F);
        final Vec3d vec31 = getVectorForRotation(pitch, yaw);
        final Vec3d vec32 = vec3.addVector(vec31.x * blockReachDistance, vec31.y * blockReachDistance, vec31.z * blockReachDistance);
        return mc.world.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    /**
     * Raytraces for a block using the given yaw/pitch, but returns null if an entity is closer (so the block is "behind" an entity).
     */
    public static HitResult rayTraceBlockIfNoEntityInFront(double reach, float yaw, float pitch) {
        if (mc.player == null || mc.world == null) return null;
        HitResult blockHit = rayTraceCustom(reach, yaw, pitch);
        Vec3d eyes = mc.player.getPositionEyes(1.0F);
        double blockDist = reach;
        if (blockHit != null && blockHit.typeOfHit == HitResult.MovingObjectType.BLOCK) {
            blockDist = blockHit.hitVec.distanceTo(eyes);
        } else {
            return null;
        }
        Vec3d lookVec = getVectorForRotation(pitch, yaw);
        Vec3d end = eyes.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        List<Entity> entities = mc.world.getEntitiesInAABBexcluding(mc.player, mc.player.getBoundingBox().addCoord(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).expand(1.0F, 1.0F, 1.0F), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        for (Entity entity : entities) {
            float border = entity.getCollisionBorderSize();
            Box aabb = entity.getBoundingBox().expand(border, border, border);
            HitResult entityHit = aabb.calculateIntercept(eyes, end);
            if (aabb.isVecInside(eyes)) {
                return null;
            }
            if (entityHit != null) {
                double entityDist = eyes.distanceTo(entityHit.hitVec);
                if (entityDist < blockDist) {
                    return null;
                }
            }
        }
        return blockHit;
    }

    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * ((float)Math.PI / 180F));
        float f3 = MathHelper.sin(-pitch * ((float)Math.PI / 180F));
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static float applyVanilla(float yaw, boolean stop) {
        if (stop) {
            return yaw;
        }
        int scaleFactor = (int) Math.floor(serverRotations[0] / 360);
        float unwrappedYaw = yaw + 360 * scaleFactor;
        if (unwrappedYaw < serverRotations[0] - 180) {
            unwrappedYaw += 360;
        }
        else if (unwrappedYaw > serverRotations[0] + 180) {
            unwrappedYaw -= 360;
        }

        float deltaYaw = unwrappedYaw - serverRotations[0];
        return serverRotations[0] + deltaYaw;
    }

    public static HitResult rayTrace(double range, float partialTicks, float[] rotations, LivingEntity ignoreCollision) {
        if (ignoreCollision != null) {
            HitResult target = rayTraceIgnore(range, partialTicks, rotations, ignoreCollision);
            if (target != null) {
                return target;
            }
        }
        Entity targetEntity = null;
        HitResult hitObject;
        double d0 = range;
        if (rotations == null) {
            rotations = new float[] { mc.player.getYaw(), mc.player.getPitch() };
        }
        hitObject = rayTraceCustom(d0, rotations[0], rotations[1]);
        double distanceTo = d0;
        Vec3d vec3 = mc.player.getPositionEyes(partialTicks);
        if (mc.interactionManager.extendedReach()) {
            d0 = 6.0;
            distanceTo = 6.0;
        }

        if (hitObject != null) {
            distanceTo = hitObject.hitVec.distanceTo(vec3);
        }

        Vec3d vec31 = RotationUtils.getVectorForRotation(rotations[1], rotations[0]);
        Vec3d vec32 = vec3.addVector(vec31.x * d0, vec31.y * d0, vec31.z * d0);
        Vec3d vec33 = null;
        float f = 1.0F;
        List<Entity> list = mc.world.getEntitiesInAABBexcluding(mc.player, mc.player.getBoundingBox().addCoord(vec31.x * d0, vec31.y * d0, vec31.z * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        double d2 = distanceTo;

        for(int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            float f1 = entity1.getCollisionBorderSize();
            Box axisalignedbb = entity1.getBoundingBox().expand(f1, f1, f1);
            HitResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0) {
                    targetEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                }
            }
            else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                if (d3 < d2 || d2 == 0.0) {
                    if (entity1 == mc.player.ridingEntity && !mc.player.canRiderInteract()) {
                        if (d2 == 0.0) {
                            targetEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                        }
                    }
                    else {
                        targetEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        if (targetEntity != null && d2 < distanceTo) {
            return new MovingObjectPosition(targetEntity, vec33);
        }
        return null;
    }

    public static HitResult rayTraceIgnore(double range, float partialTicks, float[] rotations, LivingEntity ignoreCollision) {
        HitResult blockHit = rayTraceCustom(range,
                rotations[0],
                rotations[1]);

        Vec3d start = mc.player.getPositionEyes(partialTicks);
        double blockDistance = range;
        if (blockHit != null) {
            blockDistance = blockHit.hitVec.distanceTo(start);
        }

        if (ignoreCollision != null) {
            if (rotations == null) {
                rotations = new float[]{
                        mc.player.getYaw(),
                        mc.player.getPitch()
                };
            }
            Vec3d lookVec = RotationUtils.getVectorForRotation(
                    rotations[1],  // pitch
                    rotations[0]   // yaw
            );
            Vec3d end = start.addVector(
                    lookVec.x * range,
                    lookVec.y * range,
                    lookVec.z * range
            );

            float f1 = ignoreCollision.getCollisionBorderSize();
            Box aabb = ignoreCollision.getBoundingBox()
                    .expand(f1, f1, f1);
            HitResult ignoreMOP = aabb.calculateIntercept(start, end);

            if (aabb.isVecInside(start)) {
                return new MovingObjectPosition(ignoreCollision, start);
            }
            if (ignoreMOP != null) {
                double ignoreDist = start.distanceTo(ignoreMOP.hitVec);
                if (ignoreDist < blockDistance) {
                    return new MovingObjectPosition(
                            ignoreCollision,
                            ignoreMOP.hitVec
                    );
                }
            }
        }
        if (blockHit != null) {
            return blockHit;
        }
        return null;
    }

    public static float applyVanilla(float yaw) {
        return applyVanilla(yaw, false);
    }

    public static float[] getRotationsFromEye(Vec3d eye, double tx, double ty, double tz) {
        double dx = tx - eye.x;
        double dy = ty - eye.y;
        double dz = tz - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new float[]{yaw, pitch};
    }

    public static enum PLAYER_OFFSETS {
        EYE,
        CHEST,
        FOOT,
        NONE;

        public double getHeightOffset(Entity entity) {
            switch (this) {
                case NONE:
                case EYE:
                    return entity.getEyeHeight();
                case CHEST:
                    return entity.height / 2;
                case FOOT:
                    return 0;
            }
            return entity.getEyeHeight();
        }
    }
}
