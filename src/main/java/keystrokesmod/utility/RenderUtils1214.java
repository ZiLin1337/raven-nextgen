package keystrokesmod.utility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * 1.21.4 compatible render utilities
 * Based on Meteor Client and Fabric API
 */
public class RenderUtils1214 {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Draw 2D text using DrawContext
    public static void drawText(DrawContext context, String text, int x, int y, int color) {
        context.drawTextWithShadow(mc.textRenderer, text, x, y, color);
    }
    
    // Draw 2D text centered
    public static void drawTextCentered(DrawContext context, String text, int centerX, int y, int color) {
        int width = mc.textRenderer.getWidth(text);
        context.drawTextWithShadow(mc.textRenderer, text, centerX - width / 2, y, color);
    }
    
    // Draw filled box (3D)
    public static void drawFilledBox(Box box, float r, float g, float b, float a) {
        drawFilledBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
    }
    
    public static void drawFilledBox(double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        // Bottom face
        buffer.vertex(x1, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y1, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y1, z2).color(r, g, b, a).next();
        
        // Top face
        buffer.vertex(x1, y2, z2).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z1).color(r, g, b, a).next();
        buffer.vertex(x1, y2, z1).color(r, g, b, a).next();
        
        // Front face
        buffer.vertex(x1, y1, z2).color(r, g, b, a).next();
        buffer.vertex(x2, y1, z2).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y2, z2).color(r, g, b, a).next();
        
        // Back face
        buffer.vertex(x2, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x1, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x1, y2, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z1).color(r, g, b, a).next();
        
        // Left face
        buffer.vertex(x1, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x1, y1, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y2, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y2, z1).color(r, g, b, a).next();
        
        // Right face
        buffer.vertex(x2, y1, z2).color(r, g, b, a).next();
        buffer.vertex(x2, y1, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, a).next();
        
        tessellator.draw();
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
    
    // Draw outlined box (3D)
    public static void drawOutlinedBox(Box box, float r, float g, float b, float a, float lineWidth) {
        drawOutlinedBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a, lineWidth);
    }
    
    public static void drawOutlinedBox(double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a, float lineWidth) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();
        
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        
        // Bottom face
        buffer.vertex(x1, y1, z1).color(r, g, b, a).normal(0, 0, -1).next();
        buffer.vertex(x2, y1, z1).color(r, g, b, a).normal(0, 0, -1).next();
        
        buffer.vertex(x2, y1, z1).color(r, g, b, a).normal(1, 0, 0).next();
        buffer.vertex(x2, y1, z2).color(r, g, b, a).normal(1, 0, 0).next();
        
        buffer.vertex(x2, y1, z2).color(r, g, b, a).normal(0, 0, 1).next();
        buffer.vertex(x1, y1, z2).color(r, g, b, a).normal(0, 0, 1).next();
        
        buffer.vertex(x1, y1, z2).color(r, g, b, a).normal(-1, 0, 0).next();
        buffer.vertex(x1, y1, z1).color(r, g, b, a).normal(-1, 0, 0).next();
        
        // Top face
        buffer.vertex(x1, y2, z1).color(r, g, b, a).normal(0, 0, -1).next();
        buffer.vertex(x2, y2, z1).color(r, g, b, a).normal(0, 0, -1).next();
        
        buffer.vertex(x2, y2, z1).color(r, g, b, a).normal(1, 0, 0).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, a).normal(1, 0, 0).next();
        
        buffer.vertex(x2, y2, z2).color(r, g, b, a).normal(0, 0, 1).next();
        buffer.vertex(x1, y2, z2).color(r, g, b, a).normal(0, 0, 1).next();
        
        buffer.vertex(x1, y2, z2).color(r, g, b, a).normal(-1, 0, 0).next();
        buffer.vertex(x1, y2, z1).color(r, g, b, a).normal(-1, 0, 0).next();
        
        // Vertical edges
        buffer.vertex(x1, y1, z1).color(r, g, b, a).normal(0, -1, 0).next();
        buffer.vertex(x1, y2, z1).color(r, g, b, a).normal(0, -1, 0).next();
        
        buffer.vertex(x2, y1, z1).color(r, g, b, a).normal(0, -1, 0).next();
        buffer.vertex(x2, y2, z1).color(r, g, b, a).normal(0, -1, 0).next();
        
        buffer.vertex(x2, y1, z2).color(r, g, b, a).normal(0, -1, 0).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, a).normal(0, -1, 0).next();
        
        buffer.vertex(x1, y1, z2).color(r, g, b, a).normal(0, -1, 0).next();
        buffer.vertex(x1, y2, z2).color(r, g, b, a).normal(0, -1, 0).next();
        
        tessellator.draw();
        
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
    
    // Draw 2D rect using DrawContext
    public static void drawRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y2, color);
    }
    
    // Draw gradient rect
    public static void drawGradientRect(DrawContext context, int x1, int y1, int x2, int y2, int color1, int color2) {
        context.fillGradient(x1, y1, x2, y2, color1, color2);
    }
    
    // Convert world coordinates to screen coordinates
    public static Vec3d worldToScreen(Vec3d worldPos) {
        if (mc.entityRenderDispatcher == null || mc.player == null) return null;
        
        // Get camera position
        Vec3d cameraPos = mc.entityRenderDispatcher.camera.getPos();
        
        // Translate to camera-relative coordinates
        double x = worldPos.x - cameraPos.x;
        double y = worldPos.y - cameraPos.y;
        double z = worldPos.z - cameraPos.z;
        
        // Get matrices
        Matrix4f projectionMatrix = mc.gameRenderer.getBasicProjectionMatrix(90.0f);
        Matrix4f viewMatrix = mc.entityRenderDispatcher.camera.getRotation().getRotationMatrix();
        
        // Transform
        Vector4f pos = new Vector4f((float)x, (float)y, (float)z, 1.0f);
        pos.transform(viewMatrix);
        pos.transform(projectionMatrix);
        
        // Perspective divide
        if (pos.w() == 0) return null;
        
        float ndcX = pos.x() / pos.w();
        float ndcY = pos.y() / pos.w();
        
        // Convert to screen coordinates
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        
        double screenX = (ndcX + 1.0f) / 2.0f * screenWidth;
        double screenY = (1.0f - ndcY) / 2.0f * screenHeight;
        
        return new Vec3d(screenX, screenY, pos.z());
    }
    
    // Get Identifier for texture
    public static Identifier getTexture(String namespace, String path) {
        return Identifier.of(namespace, path);
    }
    
    // Draw texture
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height) {
        context.drawTexture(texture, x, y, u, v, width, height);
    }
    
    // Draw scaled texture
    public static void drawScaledTexture(DrawContext context, Identifier texture, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
        context.drawTexture(texture, x, y, width, height, u1, v1, u2, v2, texture.getWidth(), texture.getHeight());
    }
}
