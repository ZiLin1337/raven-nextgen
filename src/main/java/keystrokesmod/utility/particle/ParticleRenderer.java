package keystrokesmod.utility.particle;

import keystrokesmod.utility.math.Vec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Renders particles using OpenGL.
 */
public class ParticleRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Render all particles in the engine.
     */
    public static void render(ParticleEngine engine) {
        if (engine.isEmpty()) return;

        Vec3 cameraPos = mc.gameRenderer.getCamera().getPos();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (Particle particle : engine.getParticles()) {
            Vec3 pos = particle.getPosition().subtract(cameraPos);
            float size = particle.getSize();
            int color = particle.getColor();
            float alpha = particle.getAlpha();

            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            float a = alpha;

            buffer.vertex(pos.x - size, pos.y - size, pos.z).color(r, g, b, a).next();
            buffer.vertex(pos.x + size, pos.y - size, pos.z).color(r, g, b, a).next();
            buffer.vertex(pos.x + size, pos.y + size, pos.z).color(r, g, b, a).next();
            buffer.vertex(pos.x - size, pos.y + size, pos.z).color(r, g, b, a).next();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        GL11.glDisable(GL11.GL_POINT_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}