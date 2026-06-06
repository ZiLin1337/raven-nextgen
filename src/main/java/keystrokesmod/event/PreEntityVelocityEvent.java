package keystrokesmod.event;

import net.minecraft.entity.Entity;

public class PreEntityVelocityEvent extends Event {
    private final Entity entity;
    private double motionX, motionY, motionZ;
    private boolean cancelled;

    public PreEntityVelocityEvent(Entity entity, double motionX, double motionY, double motionZ) {
        this.entity = entity;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public Entity getEntity() { return entity; }
    public double getMotionX() { return motionX; }
    public double getMotionY() { return motionY; }
    public double getMotionZ() { return motionZ; }
    public void setMotionX(double motionX) { this.motionX = motionX; }
    public void setMotionY(double motionY) { this.motionY = motionY; }
    public void setMotionZ(double motionZ) { this.motionZ = motionZ; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}