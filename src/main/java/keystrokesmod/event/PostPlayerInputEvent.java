package keystrokesmod.event;

public class PostPlayerInputEvent extends Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;

    public PostPlayerInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDownMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    public float getForward() { return forward; }
    public float getStrafe() { return strafe; }
    public boolean isJump() { return jump; }
    public boolean isSneak() { return sneak; }
    public double getSneakSlowDownMultiplier() { return sneakSlowDownMultiplier; }
    public void setSneakSlowDownMultiplier(double mult) { this.sneakSlowDownMultiplier = mult; }
}
