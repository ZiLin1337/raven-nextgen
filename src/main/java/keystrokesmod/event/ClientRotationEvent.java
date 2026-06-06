package keystrokesmod.event;

public class ClientRotationEvent extends Event {
    private float yaw, pitch;
    public ClientRotationEvent(float yaw, float pitch) { this.yaw = yaw; this.pitch = pitch; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
