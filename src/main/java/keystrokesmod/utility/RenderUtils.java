package keystrokesmod.utility;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RenderUtils implements IMinecraftInstance {
    private static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);
    private static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer SCREEN_COORDS = BufferUtils.createFloatBuffer(3);

    public static final class ProjectionContext {
        private int scaleFactor;
        private final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        private final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        private final IntBuffer viewport = BufferUtils.createIntBuffer(16);
        private final FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    }

    public static void renderBlock(BlockPos pos, int color, boolean outline, boolean shade) {}
    public static void renderChest(BlockPos pos, int color, boolean outline, boolean shade) {}
    public static void renderBlock(BlockPos pos, int color, double y2, boolean outline, boolean shade) {}
    public static void scissor(double x, double y, double width, double height) {}
    public static void scissorPushGui(double x, double y, double width, double height) {}
    public static void scissorPop() {}
    public static boolean isInViewFrustum(Entity entity) { return false; }
    public static boolean isInViewFrustum(Box bb) { return false; }
    public static void drawRect(double left, double top, double right, double bottom, int color) {}
    public static void drawOutline(float x, float y, float x2, float y2, float lineWidth, int color) {}
    public static void renderBox(double x, double y, double z, double x2, double y2, double z2, int color, boolean outline, boolean shade) {}
    public static void renderEntity(Entity e, int type, double expand, double shift, int color, boolean damage) {}
    public static void drawRoundedRectangle(float x, float y, float x2, float y2, float radius, int color) {}
    public static void drawRectangleGL(float x, float y, float x2, float y2, int color) {}
    public static int setAlpha(int rgb, double alpha) { return rgb; }
    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {}
    public static void drawColoredString(String text, char lineSplit, int x, int y, long s, long shift, boolean rect, TextRenderer fontRenderer) {}
    public static void drawCircle(double x, double y, double z, double radius, int sides, float lineWidth, int color, boolean chroma) {}
    public static void drawTracerLine(Entity e, int color, float lineWidth, float partialTicks) {}
    public static void renderItemAndEffectIntoGui3D(ItemStack stack, int xPos, int yPos) {}
    public static void bindTexture(int texture) {}
    public static void setAlphaLimit(float limit) {}
    public static void resetColor() {}
    public static Framebuffer createFrameBuffer(Framebuffer fb) { return fb; }
    public static Framebuffer createFrameBuffer(Framebuffer fb, boolean depth) { return fb; }
    public static boolean needsNewFramebuffer(Framebuffer fb) { return false; }
    public static void drawRoundedGradientOutlinedRectangle(float x, float y, float x2, float y2, float radius, int color1, int color2, int color3) {}
}
