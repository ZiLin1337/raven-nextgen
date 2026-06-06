package keystrokesmod.event;

public class JumpEvent extends Event {
    private double motionY;
    private float yaw;
    private boolean sprint;
    private boolean cancelled;

    public JumpEvent(double motionY, float yaw, boolean sprint) {
        this.motionY = motionY;
        this.yaw = yaw;
        this.sprint = sprint;
    }

    public double getMotionY() { return motionY; }
    public void setMotionY(double motionY) { this.motionY = motionY; }
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public boolean applySprint() { return sprint; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}