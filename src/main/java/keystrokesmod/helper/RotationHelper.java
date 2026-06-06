package keystrokesmod.helper;

import keystrokesmod.Raven;
import keystrokesmod.event.*;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.utility.RotationUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3dd;

/**
 * RotationHelper - handles silent rotation, movement fix, and raycast overrides
 * Ported from Raven1.8.9 to 1.21.4
 * Uses Meteor's orbit event bus
 */
public class RotationHelper {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final RotationHelper INSTANCE = new RotationHelper();

    private Float serverYaw = null;
    private Float serverPitch = null;
    private boolean setRotations = false;
    public boolean forceMovementFix = false;
    private boolean serverRelativeMovementInputs = false;

    // Tick-scoped swap for mouseOver
    private float savedYaw, savedPitch;
    private float savedPrevYaw, savedPrevPitch;
    public boolean swappedForMouseOver;

    private boolean rotationsUpdatedThisTick = false;

    public static RotationHelper get() { return INSTANCE; }

    public static float unwrapYaw(float yaw, float prevYaw) {
        return prevYaw + ((((yaw - prevYaw + 180f) % 360f) + 360f) % 360f - 180f);
    }

    public void updateServerRotations() {
        if (mc.player == null) return;
        if (rotationsUpdatedThisTick) return;
        rotationsUpdatedThisTick = true;

        ClientRotationEvent event = new ClientRotationEvent(
                serverYaw != null ? serverYaw : 0,
                serverPitch != null ? serverPitch : 0);
        Raven.EVENT_BUS.post(event);

        // Don't overwrite null values
        if (event.yaw != 0 || event.pitch != 0) {
            this.serverYaw = event.yaw;
            this.serverPitch = event.pitch;
        }

        if (this.serverYaw == null && this.serverPitch == null) return;

        if (this.serverYaw != null) {
            if (Math.abs(this.serverYaw - mc.player.getYaw()) >= 1.0f) {
                int randomFactor = (int) Settings.randomYawFactor.getInput();
                if (randomFactor != 0) {
                    int n = randomFactor * 100 + Utils.randomizeInt(-30, 30);
                    this.serverYaw += Utils.randomizeInt(-n, n) / 100.0f;
                }
            }
        }

        float[] fixed = RotationUtils.fixRotation(
                this.serverYaw == null ? mc.player.getYaw() : this.serverYaw,
                this.serverPitch == null ? mc.player.getPitch() : this.serverPitch,
                RotationUtils.serverRotations[0],
                RotationUtils.serverRotations[1]);
        this.serverYaw = fixed[0];
        this.serverPitch = fixed[1];

        if (this.serverYaw != mc.player.getYaw()) this.setRotations = true;
        if (this.serverPitch != mc.player.getPitch()) this.setRotations = true;
    }

    @EventHandlerpublic void onPreUpdate(PreUpdateEvent e) {
        updateServerRotations();
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!this.setRotations) return;
        if (this.serverYaw != null && !this.serverYaw.isNaN()) e.setYaw(this.serverYaw);
        if (this.serverPitch != null && !this.serverPitch.isNaN()) e.setPitch(this.serverPitch);
    }

    @EventHandler
    public void onGameTick(GameTickEvent e) {
        if (this.setRotations && this.serverYaw != null && mc.player != null) {
            float serverYawVal = RotationUtils.serverRotations[0];
            float unwrapped = unwrapYaw(MathHelper.wrapDegrees(mc.player.getYaw()), serverYawVal);
            mc.player.setYaw(unwrapped);
            mc.player.prevYaw = unwrapped;
        }
        this.serverYaw = this.serverPitch = null;
        this.setRotations = this.forceMovementFix = false;
        this.serverRelativeMovementInputs = false;
        this.rotationsUpdatedThisTick = false;
        this.swappedForMouseOver = false;
    }

    public boolean isActive() {
        return this.setRotations && (this.serverYaw != null || this.serverPitch != null);
    }

    public void beginSwap(Entity e, float yaw, float pitch, boolean swapPitch) {
        this.savedYaw = e.getYaw();
        this.savedPrevYaw = e.prevYaw;
        this.savedPitch = e.getPitch();
        this.savedPrevPitch = e.prevPitch;

        e.setYaw(yaw);
        e.prevYaw = yaw;
        if (swapPitch) {
            e.setPitch(pitch);
            e.prevPitch = pitch;
        }
    }

    public void endSwap(Entity e) {
        e.setYaw(this.savedYaw);
        e.prevYaw = this.savedPrevYaw;
        e.setPitch(this.savedPitch);
        e.prevPitch = this.savedPrevPitch;
    }

    // ----- Rotation targeting helpers -----

    public float[] getRotationsToTarget(Entity target, ClientRotationEvent e, float smoothingFactor) {
        if (target == null || mc.player == null) return null;
        float baseYaw = e.yaw;
        float basePitch = e.pitch;
        float[] rot = RotationUtils.getRotations(target, baseYaw, basePitch);
        if (rot == null) return null;
        float factor = Math.max(1f, smoothingFactor);
        float yaw = baseYaw + MathHelper.wrapDegrees(rot[0] - baseYaw) / factor;
        float pitch = basePitch + (rot[1] - basePitch) / factor;
        return new float[]{yaw, pitch};
    }

    public float[] getRotationsToTarget(Entity target, float smoothingFactor) {
        if (target == null || mc.player == null) return null;
        float baseYaw = mc.player.getYaw();
        float basePitch = mc.player.getPitch();
        float[] rot = RotationUtils.getRotations(target, baseYaw, basePitch);
        if (rot == null) return null;
        float factor = Math.max(1f, smoothingFactor);
        float yaw = baseYaw + MathHelper.wrapDegrees(rot[0] - baseYaw) / factor;
        float pitch = basePitch + (rot[1] - basePitch) / factor;
        return new float[]{yaw, pitch};
    }

    public float[] getRotationsToTarget(Entity target, ClientRotationEvent e, int speed,
                                         double horizontalMultipoint, double verticalMultipoint,
                                         float randomizationPercent, boolean useBackupPoints,
                                         double range, boolean allowThroughBlocks, boolean allowThroughEntities) {
        if (target == null || mc.player == null) return null;
        float baseYaw = e.yaw;
        float basePitch = e.pitch;
        float[] rot = useBackupPoints
                ? RotationUtils.getRotationsWithBackup(target, horizontalMultipoint, verticalMultipoint,
                baseYaw, basePitch, range, allowThroughBlocks, allowThroughEntities)
                : RotationUtils.getRotations(target, horizontalMultipoint, verticalMultipoint, baseYaw, basePitch);
        if (rot == null) return null;
        return RotationUtils.smoothRotation(baseYaw, basePitch, rot[0], rot[1], speed, randomizationPercent);
    }

    public float[] getRotationsToTarget(Entity target, int speed, double horizontalMultipoint,
                                         double verticalMultipoint, float randomizationPercent,
                                         boolean useBackupPoints, double range,
                                         boolean allowThroughBlocks, boolean allowThroughEntities) {
        if (target == null || mc.player == null) return null;
        float baseYaw = mc.player.getYaw();
        float basePitch = mc.player.getPitch();
        float[] rot = useBackupPoints
                ? RotationUtils.getRotationsWithBackup(target, horizontalMultipoint, verticalMultipoint,
                baseYaw, basePitch, range, allowThroughBlocks, allowThroughEntities)
                : RotationUtils.getRotations(target, horizontalMultipoint, verticalMultipoint, baseYaw, basePitch);
        if (rot == null) return null;
        return RotationUtils.smoothRotation(baseYaw, basePitch, rot[0], rot[1], speed, randomizationPercent);
    }

    // ----- Movement Fix -----

    @EventHandlerpublic void onPostInput(PostPlayerInputEvent event) {
        if (!fixMovement()) return;
        if (this.serverRelativeMovementInputs) return;
        if (mc.player == null) return;

        float sneakMultiplier = mc.player.input.sneaking ? 0.3F : 1F;
        float yaw = this.serverYaw != null ? this.serverYaw : mc.player.getYaw();
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;

        if (forward == 0 && strafe == 0) return;

        double angle = MathHelper.wrapDegrees(Math.toDegrees(getDirection(mc.player.getYaw(), forward, strafe)));

        float closestForward = 0, closestStrafe = 0, closestDiff = Float.MAX_VALUE;
        for (float pfRaw = -1F; pfRaw <= 1F; pfRaw += 1F) {
            for (float psRaw = -1F; psRaw <= 1F; psRaw += 1F) {
                if (pfRaw == 0 && psRaw == 0) continue;
                float predictedForward = pfRaw * sneakMultiplier;
                float predictedStrafe = psRaw * sneakMultiplier;
                double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(getDirection(yaw, predictedForward, predictedStrafe)));
                double diff = Math.abs(angle - predictedAngle);
                if (diff < closestDiff) {
                    closestDiff = (float) diff;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }
        mc.player.input.movementForward = closestForward;
        mc.player.input.movementSideways = closestStrafe;
    }

    @EventHandler
    public void onStrafe(StrafeEvent e) {
        if (fixMovement()) e.setYaw(this.serverYaw);
    }

    @EventHandler
    public void onJump(JumpEvent e) {
        if (fixMovement()) e.setYaw(this.serverYaw);
    }

    public boolean fixMovement() {
        return ((ModuleManager.movementFix != null && ModuleManager.movementFix.isEnabled())
                || this.forceMovementFix) && this.setRotations;
    }

    public static double getDirection(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;
        float forward = 1F;
        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;
        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;
        return Math.toRadians(rotationYaw);
    }

    public void setRotations(float yaw, float pitch) {
        this.serverYaw = yaw;
        this.serverPitch = pitch;
        this.setRotations = true;
    }

    public void setYaw(float yaw) { this.serverYaw = yaw; this.setRotations = true; }
    public void setPitch(float pitch) { this.serverPitch = pitch; this.setRotations = true; }
    public void setServerRelativeMovementInputs(boolean val) { this.serverRelativeMovementInputs = val; }
    public Float getServerYaw() { return serverYaw; }
    public Float getServerPitch() { return serverPitch; }
}