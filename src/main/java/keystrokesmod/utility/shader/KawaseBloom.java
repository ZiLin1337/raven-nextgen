package keystrokesmod.utility.shader;

import keystrokesmod.utility.IMinecraftInstance;
import keystrokesmod.utility.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class KawaseBloom implements IMinecraftInstance {
    public static ShaderUtils kawaseDown = new ShaderUtils("kawaseDownBloom");
    public static ShaderUtils kawaseUp = new ShaderUtils("kawaseUpBloom");
    public static net.minecraft.client.gl.Framebuffer framebuffer = new net.minecraft.client.gl.Framebuffer(1, 1, false);
    private static int currentIterations;

    private static final List<net.minecraft.client.gl.Framebuffer> framebufferList = new ArrayList<>();

    private static void initnet.minecraft.client.gl.Framebuffers(float iterations) {
        for (net.minecraft.client.gl.Framebuffer framebuffer : framebufferList) {
            framebuffer.deletenet.minecraft.client.gl.Framebuffer();
        }
        framebufferList.clear();

        framebufferList.add(framebuffer = RenderUtils.createFrameBuffer(null, false));

        for (int i = 1; i <= iterations; i++) {
            net.minecraft.client.gl.Framebuffer currentBuffer = new net.minecraft.client.gl.Framebuffer((int) (mc.displayWidth / Math.pow(2, i)), (int) (mc.displayHeight / Math.pow(2, i)), false);
            currentBuffer.setnet.minecraft.client.gl.FramebufferFilter(GL_LINEAR);

            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            GlStateManager.bindTexture(0);

            framebufferList.add(currentBuffer);
        }
    }


    public static void renderBlur(int framebufferTexture, int iterations, float offset) {
        if (currentIterations != iterations || (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight)) {
            initnet.minecraft.client.gl.Framebuffers(iterations);
            currentIterations = iterations;
        }

        RenderUtils.setAlphaLimit(0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_ONE, GL_ONE);

        GL11.glClearColor(0, 0, 0, 0);

        float currentOffset = offset;
        renderFBO(framebufferList.get(1), framebufferTexture, kawaseDown, currentOffset);

        for (int i = 1; i < iterations; i++) {
            currentOffset = offset / (float) Math.pow(1.5, i);
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, currentOffset);
        }

        for (int i = iterations; i > 1; i--) {
            currentOffset = offset / (float) Math.pow(1.5, i - 1);
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, currentOffset);
        }

        net.minecraft.client.gl.Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindnet.minecraft.client.gl.Framebuffer(false);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformi("inTexture", 0);
        kawaseUp.setUniformi("check", 1);
        kawaseUp.setUniformi("textureToCheck", 16);
        kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
        kawaseUp.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
        RenderUtils.bindTexture(framebufferTexture);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        RenderUtils.bindTexture(framebufferList.get(1).framebufferTexture);
        ShaderUtils.drawQuads();
        kawaseUp.unload();


        GlStateManager.clearColor(0, 0, 0, 0);
        mc.getnet.minecraft.client.gl.Framebuffer().bindnet.minecraft.client.gl.Framebuffer(false);
        RenderUtils.bindTexture(framebufferList.get(0).framebufferTexture);
        RenderUtils.setAlphaLimit(0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ShaderUtils.drawQuads();
        GlStateManager.bindTexture(0);
        RenderUtils.setAlphaLimit(0);

        // start blend
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void renderFBO(net.minecraft.client.gl.Framebuffer framebuffer, int framebufferTexture, ShaderUtils shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindnet.minecraft.client.gl.Framebuffer(false);
        shader.init();
        RenderUtils.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer.framebufferWidth, 1.0f / framebuffer.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        ShaderUtils.drawQuads();
        shader.unload();
    }
}
