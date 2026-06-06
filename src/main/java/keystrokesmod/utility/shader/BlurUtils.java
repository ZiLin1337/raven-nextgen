package keystrokesmod.utility.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;

public class BlurUtils {
    private static MinecraftClient mc = mc;
    private static PostEffectProcessor blurProcessor;

    public static void load() {
        if (blurProcessor == null) {
            blurProcessor = new PostEffectProcessor(mc.getTextureManager(), mc.getResourceManager(), mc.getnet.minecraft.client.gl.Framebuffer(), Identifier.of("shaders/post/blur.json"));
        }
    }

    public static void blur(float strength) {
        load();
        if (blurProcessor != null) {
            blurProcessor.setupDimensions(mc.getWindow().getnet.minecraft.client.gl.FramebufferWidth(), mc.getWindow().getnet.minecraft.client.gl.FramebufferHeight());
            blurProcessor.render(mc.getTickDelta());
        }
    }
}
