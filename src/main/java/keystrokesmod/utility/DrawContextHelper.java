package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

/**
 * 1.21.4 辅助渲染工具类
 * 提供与旧版 Gui.drawRect / drawGradientRect 等兼容的静态方法
 */
public class DrawContextHelper {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static DrawContext getContext() {
        if (mc.currentScreen != null) {
            return null; // 大部分使用场景是在HUD渲染中，通过Render2DEvent的DrawContext
        }
        return null;
    }

    /**
     * 绘制实心矩形 (兼容旧版 drawRect)
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        MatrixStack matrices = RenderSystem.getModelViewStack();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;

        buffer.vertex(matrix, (float)left, (float)bottom, 0.0f).color(r, g, b, a);
        buffer.vertex(matrix, (float)right, (float)bottom, 0.0f).color(r, g, b, a);
        buffer.vertex(matrix, (float)right, (float)top, 0.0f).color(r, g, b, a);
        buffer.vertex(matrix, (float)left, (float)top, 0.0f).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    /**
     * 绘制渐变矩形 (兼容旧版 drawGradientRect)
     */
    public static void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
        MatrixStack matrices = RenderSystem.getModelViewStack();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float r1 = (float)(color1 >> 16 & 0xFF) / 255.0f;
        float g1 = (float)(color1 >> 8 & 0xFF) / 255.0f;
        float b1 = (float)(color1 & 0xFF) / 255.0f;
        float a1 = (float)(color1 >> 24 & 0xFF) / 255.0f;
        float r2 = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color2 & 0xFF) / 255.0f;
        float a2 = (float)(color2 >> 24 & 0xFF) / 255.0f;

        buffer.vertex(matrix, (float)left, (float)bottom, 0.0f).color(r2, g2, b2, a2);
        buffer.vertex(matrix, (float)right, (float)bottom, 0.0f).color(r2, g2, b2, a2);
        buffer.vertex(matrix, (float)right, (float)top, 0.0f).color(r1, g1, b1, a1);
        buffer.vertex(matrix, (float)left, (float)top, 0.0f).color(r1, g1, b1, a1);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    /**
     * 绘制圆角矩形 (简化版)
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), color);
    }

    /**
     * 绘制边框矩形
     */
    public static void drawBorderedRect(float x, float y, float width, float height, float borderSize, int borderColor, int fillColor) {
        drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), fillColor);
        drawRect((int)x, (int)y, (int)(x + width), (int)(y + borderSize), borderColor);
        drawRect((int)x, (int)(y + height - borderSize), (int)(x + width), (int)(y + height), borderColor);
        drawRect((int)x, (int)y, (int)(x + borderSize), (int)(y + height), borderColor);
        drawRect((int)(x + width - borderSize), (int)y, (int)(x + width), (int)(y + height), borderColor);
    }

    /**
     * 绘制圆形 (简化)
     */
    public static void drawCircle(float x, float y, float radius, int color) {
        // 使用多边形近似
        MatrixStack matrices = RenderSystem.getModelViewStack();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;

        buffer.vertex(matrix, x, y, 0.0f).color(r, g, b, a);
        for (int i = 0; i <= 360; i += 10) {
            double rad = Math.toRadians(i);
            buffer.vertex(matrix, (float)(x + Math.cos(rad) * radius), (float)(y + Math.sin(rad) * radius), 0.0f).color(r, g, b, a);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }
}
