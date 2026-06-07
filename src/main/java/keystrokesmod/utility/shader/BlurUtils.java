package keystrokesmod.utility.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;

public class BlurUtils {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static PostEffectProcessor blurProcessor;

    public static void load() {
        if (blurProcessor == null)) {
            blurProcessor = new PostEffectProcessor(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), Identifier.of("shaders/post/blur.json"));
        }
    }

    public static void blur(float strength) {
        load();
        if (blurProcessor != null)) {
            blurProcessor.setupDimensions(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
            blurProcessor.render(mc.getTickDelta());
        }
    }
}
