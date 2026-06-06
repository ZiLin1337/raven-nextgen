package keystrokesmod.clickgui.animation;

public class ScrollOffsetAnimation {
    private float target, value, velocity;
    private long lastTime;

    public ScrollOffsetAnimation(long duration) {
        this.lastTime = System.currentTimeMillis();
    }

    public float getValue() {
        long now = System.currentTimeMillis();
        float delta = (now - lastTime) / 1000f;
        lastTime = now;
        
        velocity += (target - value) * delta * 10f;
        velocity *= 0.85f;
        value += velocity * delta;
        
        if (Math.abs(value - target) < 0.001f) {
            value = target;
            velocity = 0;
        }
        return value;
    }

    public float getTarget() { return target; }
    public void reset(float v) { target = value = v; velocity = 0; }
    public void extend(float delta) { target += delta; }
    public void clampTarget(float min, float max) {
        target = Math.max(min, Math.min(max, target));
    }
}
