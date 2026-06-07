package keystrokesmod.utility.particle;

import keystrokesmod.utility.math.Vec3d;

/**
 * Single particle with position, velocity, and lifetime.
 */
public class Particle {
    private Vec3d position;
    private Vec3d velocity;
    private Vec3d acceleration;
    private int lifetime;
    private int maxLifetime;
    private float size;
    private int color;
    private float alpha;
    private boolean dead;

    public Particle(Vec3d position, Vec3d velocity, int lifetime, float size, int color) {
        this.position = position.clone();
        this.velocity = velocity.clone();
        this.acceleration = new Vec3d(0, -0.05, 0); // Gravity
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.size = size;
        this.color = color;
        this.alpha = 1.0f;
        this.dead = false;
    }

    public void update() {
        if (dead) return;
        velocity = velocity.add(acceleration);
        position = position.add(velocity);
        lifetime--;
        if (lifetime <= 0) dead = true;
        alpha = Math.max(0, (float) lifetime / maxLifetime);
    }

    public Vec3d getPosition() { return position; }
    public Vec3d getVelocity() { return velocity; }
    public float getSize() { return size; }
    public int getColor() { return color; }
    public float getAlpha() { return alpha; }
    public boolean isDead() { return dead; }
    public int getLifetime() { return lifetime; }
    public int getMaxLifetime() { return maxLifetime; }

    public void setPosition(Vec3d position) { this.position = position; }
    public void setVelocity(Vec3d velocity) { this.velocity = velocity; }
    public void setAcceleration(Vec3d acceleration) { this.acceleration = acceleration; }
    public void setSize(float size) { this.size = size; }
    public void setColor(int color) { this.color = color; }
}