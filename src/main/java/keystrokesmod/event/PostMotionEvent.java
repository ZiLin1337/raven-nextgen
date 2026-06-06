package keystrokesmod.event;

import net.minecraft.util.math.Vec3d;

public class PostMotionEvent extends Event {
    private double posX, posY, posZ;
    private float yaw, pitch;
    private boolean onGround;
    public PostMotionEvent(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        this.posX = posX; this.posY = posY; this.posZ = posZ;
        this.yaw = yaw; this.pitch = pitch; this.onGround = onGround;
    }
    public double getPosX() { return posX; } public double getPosY() { return posY; } public double getPosZ() { return posZ; }
    public float getYaw() { return yaw; } public float getPitch() { return pitch; }
    public boolean isOnGround() { return onGround; }
}
