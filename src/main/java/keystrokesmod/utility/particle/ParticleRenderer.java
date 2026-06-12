package keystrokesmod.utility.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleRenderer {
    private static final List<ParticleData> particles = new ArrayList<>();
    
    public static void addParticle(Vec3d pos, Vec3d velocity, int color, int lifetime) {
        particles.add(new ParticleData(pos, velocity, color, lifetime, 0));
    }
    
    public static void update() {
        Iterator<ParticleData> it = particles.iterator();
        while (it.hasNext()) {
            ParticleData p = it.next();
            p.age++;
            if (p.age >= p.lifetime) {
                it.remove();
            } else {
                p.pos = p.pos.add(p.velocity);
                p.velocity = p.velocity.multiply(0.98);
            }
        }
    }
    
    public static List<ParticleData> getParticles() {
        return particles;
    }
    
    public static void clear() {
        particles.clear();
    }
    
    public static void render(Object engine) {
        // Render particles using OpenGL
    }
    
    public static class ParticleData {
        public Vec3d pos;
        public Vec3d velocity;
        public int color;
        public int lifetime;
        public int age;
        
        public ParticleData(Vec3d pos, Vec3d velocity, int color, int lifetime, int age) {
            this.pos = pos;
            this.velocity = velocity;
            this.color = color;
            this.lifetime = lifetime;
            this.age = age;
        }
    }
}
