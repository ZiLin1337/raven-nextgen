package keystrokesmod.event;

import keystrokesmod.utility.Utils;

public class PreMotionEvent extends Event {
    private double posX, posY, posZ;
    private float yaw, pitch;
    private boolean onGround, isSprinting, isSneaking;
    private static boolean setRenderYaw;
    public static boolean setRotations;
    public static float preMotionYaw;

    public PreMotionEvent(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking) {
        this.posX = posX; this.posY = posY; this.posZ = posZ;
        this.yaw = yaw; this.pitch = pitch;
        this.isOnGround() = onGround; this.isSprinting = isSprinting; this.isSneaking = isSneaking;
    }

    public double getPosX() { return posX; } public void setPosX(double posX) { this.posX = posX; }
    public double getPosY() { return posY; } public void setPosY(double posY) { this.posY = posY; }
    public double getPosZ() { return posZ; } public void setPosZ(double posZ) { this.posZ = posZ; }
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; this.setRenderYaw = true; setRotations = true; preMotionYaw = yaw; }
    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; setRotations = true; }
    public void setRotations(float yaw, float pitch) { this.yaw = yaw; this.pitch = pitch; this.setRenderYaw = true; setRotations = true; preMotionYaw = yaw; }
    public boolean isOnGround() { return onGround; } public void setOnGround(boolean onGround) { this.isOnGround() = onGround; }
    public static boolean setRenderYaw() { return setRenderYaw; }
    public static void setRenderYaw(boolean setYaw) { setRenderYaw = setYaw; }
    public boolean isSprinting() { return isSprinting; } public void setSprinting(boolean sprinting) { this.isSprinting = sprinting; }
    public boolean isSneaking() { return isSneaking; } public void setSneaking(boolean sneaking) { this.isSneaking = sneaking; }
}
