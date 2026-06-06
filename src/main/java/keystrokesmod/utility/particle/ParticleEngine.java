package keystrokesmod.utility.particle;

import keystrokesmod.utility.math.Vec3;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Particle physics engine for managing and rendering particles.
 */
public class ParticleEngine {
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private int maxParticles = 1000;

    public ParticleEngine() {}

    public ParticleEngine(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    /**
     * Spawn a single particle.
     */
    public void spawn(Particle particle) {
        if (particles.size() < maxParticles) {
            particles.add(particle);
        }
    }

    /**
     * Spawn multiple particles at once.
     */
    public void spawn(List<Particle> newParticles) {
        for (Particle p : newParticles) {
            if (particles.size() < maxParticles) {
                particles.add(p);
            }
        }
    }

    /**
     * Spawn a burst of particles at a position with random velocities.
     */
    public void burst(Vec3 position, int count, float speed, int lifetime, float size, int color) {
        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * speed;
            double vy = random.nextDouble() * speed;
            double vz = (random.nextDouble() - 0.5) * speed;
            Vec3 velocity = new Vec3(vx, vy, vz);
            int lt = lifetime + random.nextInt(lifetime / 2);
            float sz = size * (0.5f + random.nextFloat());
            spawn(new Particle(position, velocity, lt, sz, color));
        }
    }

    /**
     * Spawn a spray of particles in a direction.
     */
    public void spray(Vec3 position, Vec3 direction, int count, float spread, float speed, int lifetime, float size, int color) {
        for (int i = 0; i < count; i++) {
            Vec3 vel = new Vec3(
                direction.x + (random.nextDouble() - 0.5) * spread,
                direction.y + (random.nextDouble() - 0.5) * spread,
                direction.z + (random.nextDouble() - 0.5) * spread
            ).normalize().multiply(speed * (0.8f + random.nextFloat() * 0.4f));
            int lt = lifetime + random.nextInt(lifetime / 3);
            spawn(new Particle(position, vel, lt, size, color));
        }
    }

    /**
     * Update all particles.
     */
    public void update() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.isDead()) {
                it.remove();
            }
        }
    }

    /**
     * Clear all particles.
     */
    public void clear() {
        particles.clear();
    }

    /**
     * Get all active particles.
     */
    public List<Particle> getParticles() {
        return particles;
    }

    /**
     * Get particle count.
     */
    public int getParticleCount() {
        return particles.size();
    }

    /**
     * Check if there are any particles.
     */
    public boolean isEmpty() {
        return particles.isEmpty();
    }
}