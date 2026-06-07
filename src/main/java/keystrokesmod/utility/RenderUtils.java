package keystrokesmod.utility;

import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.mixin.impl.accessor.IAccessorMinecraft;
import keystrokesmod.module.impl.player.Freecam;

import net.minecraft.block.StairsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.Frustum;

// removed IBakedModel
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import keystrokesmod.Raven;
import keystrokesmod.utility.StairsUtils;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils implements IMinecraftInstance {
    private static Frustum frustum = new Frustum(mc.getEntityRenderDispatcher().camera.getProjection());

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

    public static void renderBlock(BlockPos blockPos, int color, boolean outline, boolean shade) {
        renderBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, outline, shade);
    }

    public static void renderChest(BlockPos blockPos, int color, boolean outline, boolean shade) {
        renderBox(blockPos.getX() + 0.0625F, blockPos.getY(), blockPos.getZ() + 0.0625F, 0.875f, 0.875f, 0.875f, color, outline, shade);
    }

    public static void renderChestBatch(List<BlockPos> positions, int color, boolean outline, boolean shade) {
        renderChestBatch(positions, color, color, outline, shade);
    }

    public static void renderChestBatch(List<BlockPos> positions, int outlineColor, int shadeColor, boolean outline, boolean shade) {
        if (positions == null || positions.isEmpty()) {
            return;
        }
        double vx = mc.getEntityRenderDispatcher().camera.getPos().x;
        double vy = mc.getEntityRenderDispatcher().camera.getPos().y;
        double vz = mc.getEntityRenderDispatcher().camera.getPos().z;
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        float outlineA = (outlineColor >> 24 & 0xFF) / 255.0f;
        float outlineR = (outlineColor >> 16 & 0xFF) / 255.0f;
        float outlineG = (outlineColor >> 8 & 0xFF) / 255.0f;
        float outlineB = (outlineColor & 0xFF) / 255.0f;
        float shadeA = (shadeColor >> 24 & 0xFF) / 255.0f;
        float shadeR = (shadeColor >> 16 & 0xFF) / 255.0f;
        float shadeG = (shadeColor >> 8 & 0xFF) / 255.0f;
        float shadeB = (shadeColor & 0xFF) / 255.0f;
        for (BlockPos blockPos : positions) {
            double xPos = blockPos.getX() + 0.0625 - vx;
            double yPos = blockPos.getY() - vy;
            double zPos = blockPos.getZ() + 0.0625 - vz;
            Box axisAlignedBB = new Box(xPos, yPos, zPos, xPos + 0.875, yPos + 0.875, zPos + 0.875);
            if (outline) {
                RenderSystem.setShaderColor(outlineR, outlineG, outlineB, outlineA);
                // drawSelectionBoundingBox moved: axisAlignedBB);
            }
            if (shade) {
                drawBoundingBox(axisAlignedBB);
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void renderBlock(BlockPos blockPos, int color, double y2, boolean outline, boolean shade) {
        renderBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, y2, 1, color, outline, shade);
    }

    /**
     * Sets scissor in GUI coords (origin top-left). Uses edge-aware rounding so
     * the clip rectangle does not lose/gain a pixel at animation boundaries.
     */
    public static void scissor(double x, double y, double width, double height) {
        double guiScale = ClickGui.getActiveRenderScale();
        x *= guiScale;
        y *= guiScale;
        width *= guiScale;
        height *= guiScale;

        ScaledResolution sr = new ScaledResolution(mc);
        int scale = mc.getWindow().getScaleFactor();
        double screenH = mc.getWindow().getScaledHeight();

        int left = (int) Math.floor(x * scale);
        int right = (int) Math.ceil((x + width) * scale);
        int scaledWidth = Math.max(0, right - left);

        double bottomGui = y + height;
        int glBottom = (int) Math.floor((screenH - bottomGui) * scale);
        int glTop = (int) Math.ceil((screenH - y) * scale);
        int scaledHeight = Math.max(0, glTop - glBottom);

        if (scaledWidth < 0 || scaledHeight < 0) {
            return;
        }

        RenderSystem.scissor(left, glBottom, scaledWidth, scaledHeight);
    }

    private static final int SCISSOR_PUSH_STACK_DEPTH = 4;
    private static final IntBuffer SCISSOR_PUSH_BUF = BufferUtils.createIntBuffer(16);
    private static final int[][] scissorPushStack = new int[SCISSOR_PUSH_STACK_DEPTH][5];
    private static int scissorPushDepth = 0;

    public static void scissorPushGui(double x, double y, double width, double height) {
        double guiScale = ClickGui.getActiveRenderScale();
        x *= guiScale;
        y *= guiScale;
        width *= guiScale;
        height *= guiScale;

        ScaledResolution sr = new ScaledResolution(mc);
        int scale = mc.getWindow().getScaleFactor();
        double screenH = mc.getWindow().getScaledHeight();
        int left = (int) Math.floor(x * scale);
        int right = (int) Math.ceil((x + width) * scale);
        int scaledWidth = Math.max(0, right - left);
        double bottomGui = y + height;
        int glBottom = (int) Math.floor((screenH - bottomGui) * scale);
        int glTop = (int) Math.ceil((screenH - y) * scale);
        int scaledHeight = Math.max(0, glTop - glBottom);
        boolean wasEnabled = GL11.isEnabled(GL11.GL_SCISSOR_TEST);
        int[] saved = scissorPushStack[scissorPushDepth++];
        if (scissorPushDepth > SCISSOR_PUSH_STACK_DEPTH) throw new IllegalStateException("Scissor stack overflow");
        if (wasEnabled) {
            SCISSOR_PUSH_BUF.clear();
            saved[0] = 1;
            saved[1] = SCISSOR_PUSH_BUF.get(0);
            saved[2] = SCISSOR_PUSH_BUF.get(1);
            saved[3] = SCISSOR_PUSH_BUF.get(2);
            saved[4] = SCISSOR_PUSH_BUF.get(3);
            int ix = Math.max(saved[1], left);
            int iy = Math.max(saved[2], glBottom);
            int iw = Math.max(0, Math.min(saved[1] + saved[3], left + scaledWidth) - ix);
            int ih = Math.max(0, Math.min(saved[2] + saved[4], glBottom + scaledHeight) - iy);
            RenderSystem.scissor(ix, iy, iw, ih);
        } else {
            saved[0] = 0;
            // GL_SCISSOR_TEST (removed);
            RenderSystem.scissor(left, glBottom, scaledWidth, scaledHeight);
        }
    }

    public static void scissorPop() {
        int[] saved = scissorPushStack[--scissorPushDepth];
        if (saved[0] == 1) {
            RenderSystem.scissor(saved[1], saved[2], saved[3], saved[4]);
        } else {
            // GL_SCISSOR_TEST (removed);
        }
    }

    public static boolean isInViewFrustum(final Entity entity) {
        if (entity == null) return false;
        return isInViewFrustum(entity.getBoundingBox()) || ignoreFrustumCheck;
    }

    public static boolean isInViewFrustum(final Box bb) {
        if (bb == null) return false;
        Entity view = mc.getRenderViewEntity();
        if (view == null) return true;
        frustum.setPosition(view.getX(), view.getY(), view.getZ());
        return frustum.isVisible(bb);
    }

    public static boolean isWithinDistanceSqToRenderView(final Entity entity, final double maxDistSq) {
        if (entity == null) return false;
        Entity view = mc.getRenderViewEntity();
        if (view == null) return false;
        return entity.squaredDistanceTo(view) <= maxDistSq;
    }

    public static boolean isBlockPosWithinDistanceSqToView(final BlockPos pos, final double maxDistSq) {
        if (pos == null) return false;
        Entity view = mc.getRenderViewEntity();
        if (view == null) return false;
        double dx = pos.getX() + 0.5 - view.getX();
        double dy = pos.getY() + 0.5 - view.getY();
        double dz = pos.getZ() + 0.5 - view.getZ();
        return dx * dx + dy * dy + dz * dz <= maxDistSq;
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        float f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        RenderSystem.getModelViewStack().pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.setShaderColor(f, f1, f2, f3);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.pos(left, bottom, 0.0D).next();
        bufferBuilder.pos(right, bottom, 0.0D).next();
        bufferBuilder.pos(right, top, 0.0D).next();
        bufferBuilder.pos(left, top, 0.0D).next();
        BufferRenderer.drawWithGlobalProgram(wr.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawPlayerBoundingBox(Vec3d pos, int color) {
        RenderSystem.getModelViewStack().pushMatrix();
        double x = pos.xCoord - mc.getEntityRenderDispatcher().camera.getPos().x;
        double y = pos.yCoord - mc.getEntityRenderDispatcher().camera.getPos().y;
        double z = pos.zCoord - mc.getEntityRenderDispatcher().camera.getPos().z;
        Box bbox = mc.player.getBoundingBox().expand(0.1D, 0.1, 0.1);
        Box axis = new Box(bbox.minX - mc.player.getX() + x, bbox.minY - mc.player.getY() + y, bbox.minZ - mc.player.getZ() + z, bbox.maxX - mc.player.getX() + x, bbox.maxY - mc.player.getY() + y, bbox.maxZ - mc.player.getZ() + z);
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend(3042);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(2.0F);
        RenderSystem.setShaderColor(r, g, b, a);
        drawBoundingBox(axis);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawOutline(float x, float y, float x2, float y2, float lineWidth, int color) {
        float f5 = (float) ((color >> 24) & 255) / 255.0F;
        float f6 = (float) ((color >> 16) & 255) / 255.0F;
        float f7 = (float) ((color >> 8) & 255) / 255.0F;
        float f8 = (float) (color & 255) / 255.0F;

        RenderSystem.disableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.setShaderColor(f6, f7, f8, f5);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
    }

    public static void renderBox(double x, double y, double z, double x2, double y2, double z2, int color, boolean outline, boolean shade) {
        double xPos = x - mc.getEntityRenderDispatcher().camera.getPos().x;
        double yPos = y - mc.getEntityRenderDispatcher().camera.getPos().y;
        double zPos = z - mc.getEntityRenderDispatcher().camera.getPos().z;
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        float n8 = (color >> 24 & 0xFF) / 255.0f;
        float n9 = (color >> 16 & 0xFF) / 255.0f;
        float n10 = (color >> 8 & 0xFF) / 255.0f;
        float n11 = (color & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(n9, n10, n11, n8);
        Box axisAlignedBB = new Box(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
        if (outline) {
            // drawSelectionBoundingBox moved: axisAlignedBB);
        }
        if (shade) {
            drawBoundingBox(axisAlignedBB);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    /**
     * Renders only the given faces of a full block at pos (no double render when adjacent blocks are also highlighted).
     * Caller should pass only faces whose neighbor is not in the same highlight set.
     * Shaded fill uses 0.25f alpha to match drawBoundingBox; outline uses full color.
     */
    public static void renderBlockFaces(BlockPos blockPos, int color, boolean outline, boolean shade, java.util.Set<Direction> faces) {
        if (faces == null || faces.isEmpty()) return;
        double xPos = blockPos.getX() - mc.getEntityRenderDispatcher().camera.getPos().x;
        double yPos = blockPos.getY() - mc.getEntityRenderDispatcher().camera.getPos().y;
        double zPos = blockPos.getZ() - mc.getEntityRenderDispatcher().camera.getPos().z;
        double maxX = xPos + 1;
        double maxY = yPos + 1;
        double maxZ = zPos + 1;
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float outlineA = (color >> 24 & 0xFF) / 255.0f;
        float shadeA = 0.25f;
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        if (shade) {
            vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            if (faces.contains(Direction.DOWN)) {
                vb.pos(xPos, yPos, zPos).color(r, g, b, shadeA).next();
                vb.pos(maxX, yPos, zPos).color(r, g, b, shadeA).next();
                vb.pos(maxX, yPos, maxZ).color(r, g, b, shadeA).next();
                vb.pos(xPos, yPos, maxZ).color(r, g, b, shadeA).next();
            }
            if (faces.contains(Direction.UP)) {
                vb.pos(xPos, maxY, zPos).color(r, g, b, shadeA).next();
                vb.pos(xPos, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, zPos).color(r, g, b, shadeA).next();
            }
            if (faces.contains(Direction.NORTH)) {
                vb.pos(xPos, yPos, zPos).color(r, g, b, shadeA).next();
                vb.pos(xPos, maxY, zPos).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, zPos).color(r, g, b, shadeA).next();
                vb.pos(maxX, yPos, zPos).color(r, g, b, shadeA).next();
            }
            if (faces.contains(Direction.SOUTH)) {
                vb.pos(maxX, yPos, maxZ).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(xPos, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(xPos, yPos, maxZ).color(r, g, b, shadeA).next();
            }
            if (faces.contains(Direction.WEST)) {
                vb.pos(xPos, yPos, zPos).color(r, g, b, shadeA).next();
                vb.pos(xPos, maxY, zPos).color(r, g, b, shadeA).next();
                vb.pos(xPos, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(xPos, yPos, maxZ).color(r, g, b, shadeA).next();
            }
            if (faces.contains(Direction.EAST)) {
                vb.pos(maxX, yPos, maxZ).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, maxZ).color(r, g, b, shadeA).next();
                vb.pos(maxX, maxY, zPos).color(r, g, b, shadeA).next();
                vb.pos(maxX, yPos, zPos).color(r, g, b, shadeA).next();
            }
            ts.draw();
        }
        if (outline) {
            RenderSystem.setShaderColor(r, g, b, outlineA);
            vb.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION);
            if (faces.contains(Direction.DOWN)) {
                vb.pos(xPos, yPos, zPos).next(); vb.pos(maxX, yPos, zPos).next();
                vb.pos(maxX, yPos, zPos).next(); vb.pos(maxX, yPos, maxZ).next();
                vb.pos(maxX, yPos, maxZ).next(); vb.pos(xPos, yPos, maxZ).next();
                vb.pos(xPos, yPos, maxZ).next(); vb.pos(xPos, yPos, zPos).next();
            }
            if (faces.contains(Direction.UP)) {
                vb.pos(xPos, maxY, zPos).next(); vb.pos(maxX, maxY, zPos).next();
                vb.pos(maxX, maxY, zPos).next(); vb.pos(maxX, maxY, maxZ).next();
                vb.pos(maxX, maxY, maxZ).next(); vb.pos(xPos, maxY, maxZ).next();
                vb.pos(xPos, maxY, maxZ).next(); vb.pos(xPos, maxY, zPos).next();
            }
            if (faces.contains(Direction.NORTH)) {
                vb.pos(xPos, yPos, zPos).next(); vb.pos(xPos, maxY, zPos).next();
                vb.pos(xPos, maxY, zPos).next(); vb.pos(maxX, maxY, zPos).next();
                vb.pos(maxX, maxY, zPos).next(); vb.pos(maxX, yPos, zPos).next();
                vb.pos(maxX, yPos, zPos).next(); vb.pos(xPos, yPos, zPos).next();
            }
            if (faces.contains(Direction.SOUTH)) {
                vb.pos(xPos, yPos, maxZ).next(); vb.pos(xPos, maxY, maxZ).next();
                vb.pos(xPos, maxY, maxZ).next(); vb.pos(maxX, maxY, maxZ).next();
                vb.pos(maxX, maxY, maxZ).next(); vb.pos(maxX, yPos, maxZ).next();
                vb.pos(maxX, yPos, maxZ).next(); vb.pos(xPos, yPos, maxZ).next();
            }
            if (faces.contains(Direction.WEST)) {
                vb.pos(xPos, yPos, zPos).next(); vb.pos(xPos, maxY, zPos).next();
                vb.pos(xPos, maxY, zPos).next(); vb.pos(xPos, maxY, maxZ).next();
                vb.pos(xPos, maxY, maxZ).next(); vb.pos(xPos, yPos, maxZ).next();
                vb.pos(xPos, yPos, maxZ).next(); vb.pos(xPos, yPos, zPos).next();
            }
            if (faces.contains(Direction.EAST)) {
                vb.pos(maxX, yPos, zPos).next(); vb.pos(maxX, maxY, zPos).next();
                vb.pos(maxX, maxY, zPos).next(); vb.pos(maxX, maxY, maxZ).next();
                vb.pos(maxX, maxY, maxZ).next(); vb.pos(maxX, yPos, maxZ).next();
                vb.pos(maxX, yPos, maxZ).next(); vb.pos(maxX, yPos, zPos).next();
            }
            ts.draw();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    private static void drawBoxFaceVertex(BufferBuilder wr, double x, double y, double z, int color) {
        wr.pos(x, y, z).color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF).next();
    }

    private static void drawBoxFaceVertices(BufferBuilder wr, Direction face, Box box, int start, int end) {
        switch (face) {
            case UP:
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.maxZ, end);
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.minZ, start);
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.minZ, end);
                break;
            case DOWN:
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.maxZ, end);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.minZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.minZ, end);
                break;
            case NORTH:
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.minZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.minZ, end);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.minZ, start);
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.minZ, end);
                break;
            case SOUTH:
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.maxZ, end);
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.maxZ, end);
                break;
            case EAST:
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.minZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.maxY, box.maxZ, end);
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.maxX, box.minY, box.minZ, end);
                break;
            case WEST:
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.maxZ, start);
                drawBoxFaceVertex(wr, box.minX, box.maxY, box.minZ, end);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.minZ, start);
                drawBoxFaceVertex(wr, box.minX, box.minY, box.maxZ, end);
                break;
        }
    }

    /**
     * Draws one face of an AABB (box in current GL space). Caller must have set blend, disabled texture, etc.
     */
    public static void drawBoxFace(Box box, Direction face, int overlayColor, int outlineColor, boolean overlay, boolean outline) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder wr = ts.getBuffer();
        if (overlay) {
            wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            drawBoxFaceVertices(wr, face, box, overlayColor, overlayColor);
            ts.draw();
        }
        if (outline) {
            wr.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
            drawBoxFaceVertices(wr, face, box, outlineColor, outlineColor);
            ts.draw();
        }
    }

    public static void renderBlockShape(BlockPos pos, BlockState state, int color, boolean outline, boolean shade, java.util.Set<Direction> visibleFaces) {
        Box box = BlockUtils.getBlockSelectionBox(pos);
        if (box == null) return;
        double vx = mc.getEntityRenderDispatcher().camera.getPos().x, vy = mc.getEntityRenderDispatcher().camera.getPos().y, vz = mc.getEntityRenderDispatcher().camera.getPos().z;
        int overlayColor = (color & 0x00FFFFFF) | (63 << 24);
        int outlineColor = color | 0xFF000000;

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        if (state.getBlock() instanceof StairsBlock) {
            StairsUtils.drawStairs(pos, state, box, null, vx, vy, vz, overlayColor, outlineColor, outlineColor, outlineColor, shade, outline, (b, face, os, oe, ls, le, ov, ol) -> drawBoxFace(b, face, overlayColor, outlineColor, ov, ol));
        } else {
            Box renderBox = box.offset(-vx, -vy, -vz);
            for (Direction face : visibleFaces) {
                drawBoxFace(renderBox, face, overlayColor, outlineColor, shade, outline);
            }
        }

        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void renderBPS(final boolean b, final boolean b2) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        String s = "";
        int n = -1;
        if (b) {
            final double t = Utils.gbps((Freecam.freeEntity == null) ? mc.player : Freecam.freeEntity, 2);
            if (t < 10.0) {
                n = Color.green.getRGB();
            }
            else if (t < 30.0) {
                n = Color.yellow.getRGB();
            }
            else if (t < 60.0) {
                n = Color.orange.getRGB();
            }
            else if (t < 160.0) {
                n = Color.red.getRGB();
            }
            else {
                n = Color.black.getRGB();
            }
            s = s + t + "bps";
        }
        if (b2) {
            final double h = Utils.getHorizontalSpeed();
            if (!s.isEmpty()) {
                s += " ";
            }
            s += Utils.round(h, 3);
        }
        mc.textRenderer.draw(s, (float)(scaledResolution.getScaledWidth() / 2 - mc.textRenderer.getStringWidth(s) / 2), (float)(scaledResolution.getScaledHeight() / 2 + 15), n, false);
    }

    public static void renderEntity(Entity e, int type, double expand, double shift, int color, boolean damage) {
        if (e instanceof LivingEntity) {
            float partialTicks = ((IAccessorMinecraft) mc).getLastFrameDuration();
            double x = e.prevX + (e.getX() - e.prevX) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().x;
            double y = e.prevY + (e.getY() - e.prevY) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().y;
            double z = e.prevZ + (e.getZ() - e.prevZ) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().z;
            float d = (float) expand / 40.0F;
            if (e instanceof PlayerEntity && damage && ((PlayerEntity) e).hurtTime != 0) {
                color = Color.RED.getRGB();
            }

            RenderSystem.getModelViewStack().pushMatrix();
            if (type == 3) {
                RenderSystem.translate(x, y - 0.2D, z);
                RenderSystem.rotate((double) (-mc.getEntityRenderDispatcher().camera.getYaw()), 0.0D, 1.0D, 0.0D);
                RenderSystem.disableDepth();
                RenderSystem.scale(0.03F + d, 0.03F + d, 0.03F + d);
                int outline = Color.black.getRGB();
                DrawContextHelper.drawRect(-20, -1, -26, 75, outline);
                DrawContextHelper.drawRect(20, -1, 26, 75, outline);
                DrawContextHelper.drawRect(-20, -1, 21, 5, outline);
                DrawContextHelper.drawRect(-20, 70, 21, 75, outline);
                if (color != 0) {
                    DrawContextHelper.drawRect(-21, 0, -25, 74, color);
                    DrawContextHelper.drawRect(21, 0, 25, 74, color);
                    DrawContextHelper.drawRect(-21, 0, 24, 4, color);
                    DrawContextHelper.drawRect(-21, 71, 25, 74, color);
                }
                else {
                    int st = Utils.getChroma(2L, 0L);
                    int en = Utils.getChroma(2L, 1000L);
                    DrawContextHelper.drawGradientRect(-21, 0, -25, 74, st, en);
                    DrawContextHelper.drawGradientRect(21, 0, 25, 74, st, en);
                    DrawContextHelper.drawRect(-21, 0, 21, 4, en);
                    DrawContextHelper.drawRect(-21, 71, 21, 74, st);
                }

                RenderSystem.enableDepth();
            }
            else {
                int i;
                if (type == 4) {
                    LivingEntity en = (LivingEntity) e;
                    double health = en.getHealth() / en.getMaxHealth();
                    int barHeight = (int) (74.0D * health);
                    int healthColor = health < 0.3D ? Color.red.getRGB() : (health < 0.5D ? Color.orange.getRGB() : (health < 0.7D ? Color.yellow.getRGB() : Color.green.getRGB()));
                    RenderSystem.translate(x, y - 0.2D, z);
                    RenderSystem.rotate(-mc.getEntityRenderDispatcher().camera.getYaw(), 0.0D, 1.0D, 0.0D);
                    RenderSystem.disableDepth();
                    RenderSystem.scale(0.03F + d, 0.03F + d, 0.03F + d);
                    i = (int) (21 + shift * 2);
                    DrawContextHelper.drawRect(i, -1, i + 4, 75, Color.black.getRGB());
                    DrawContextHelper.drawRect(i + 1, barHeight, i + 3, 74, Color.darkGray.getRGB());
                    DrawContextHelper.drawRect(i + 1, 0, i + 3, barHeight, healthColor);
                    RenderSystem.enableDepth();
                }
                else if (type == 6) {
                    DrawContextHelper.drawCircle(x, y, z, 0.699999988079071D, 45, 1.5F, color, color == 0);
                }
                else {
                    if (color == 0) {
                        color = Utils.getChroma(2L, 0L);
                    }

                    float a = (float) (color >> 24 & 255) / 255.0F;
                    float r = (float) (color >> 16 & 255) / 255.0F;
                    float g = (float) (color >> 8 & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;
                    Box bbox = e.getBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand);
                    Box axis = new Box(bbox.minX - e.getX() + x, bbox.minY - e.getY() + y, bbox.minZ - e.getZ() + z, bbox.maxX - e.getX() + x, bbox.maxY - e.getY() + y, bbox.maxZ - e.getZ() + z);
                    RenderSystem.blendFunc(770, 771);
                    RenderSystem.enableBlend();
                    RenderSystem.disableTexture();
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.lineWidth(2.0F);
                    RenderSystem.setShaderColor(r, g, b, a);
                    if (type == 1) {
                        // drawSelectionBoundingBox moved: axis);
                    } else if (type == 2) {
                        drawBoundingBox(axis);
                    }
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthMask(true);
                    RenderSystem.disableBlend();
                }
            }
            RenderSystem.getModelViewStack().popMatrix();
        }
    }

    public static void drawPolygon(final double n, final double n2, final double n3, final int n4, final int n5) {
        if (n4 < 3) {
            return;
        }
        final float n6 = (n5 >> 24 & 0xFF) / 255.0f;
        final float n7 = (n5 >> 16 & 0xFF) / 255.0f;
        final float n8 = (n5 >> 8 & 0xFF) / 255.0f;
        final float n9 = (n5 & 0xFF) / 255.0f;
        final Tessellator getInstance = Tessellator.getInstance();
        final BufferBuilder getBufferBuilder = getInstance.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.setShaderColor(n7, n8, n9, n6);
        getBufferBuilder.begin(6, VertexFormats.POSITION);
        for (int i = 0; i < n4; ++i) {
            final double n10 = 6.283185307179586 * i / n4 + Math.toRadians(180.0);
            getBufferBuilder.pos(n + Math.sin(n10) * n3, n2 + Math.cos(n10) * n3, 0.0).next();
        }
        getInstance.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Draws a 12-edge wireframe outline of an AABB in world space.
     * Caller must set RenderSystem.lineWidth and GL11.glColor before calling.
     */
    public static void drawOutlinedBox(Box worldBox, double viewerX, double viewerY, double viewerZ) {
        Box renderBox = worldBox.offset(-viewerX, -viewerY, -viewerZ);
        // drawSelectionBoundingBox moved: renderBox);
    }

    public static void drawBoundingBox(Box abb) {
        drawBoundingBox(abb);
    }

    public static void drawBoundingBox(Box abb) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        ts.draw();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        ts.draw();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        ts.draw();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        ts.draw();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        ts.draw();
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).next();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).next();
        ts.draw();
    }

    public static void renderBlockModel(BlockState blockState, BlockPos blockPos, int color) {
        renderBlockModel(blockState, blockPos.getX(), blockPos.getY(), blockPos.getZ(), color);
    }

    public static void renderBlockModel(BlockState blockState, double x, double y, double z, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelFromBlockState(blockState, mc.world, new BlockPos(x, y, z));

        double xPos = x - mc.getEntityRenderDispatcher().camera.getPos().x;
        double yPos = y - mc.getEntityRenderDispatcher().camera.getPos().y;
        double zPos = z - mc.getEntityRenderDispatcher().camera.getPos().z;

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8)  & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.translate(xPos, yPos, zPos);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        RenderSystem.disableDepth();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(r, g, b, a);

        renderModelColoredQuads(model, r, g, b, a);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepth();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    private static void renderModelColoredQuads(IBakedModel model, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        for (Direction face : Direction.values()) {
            for (BakedQuad quad : model.getFaceQuads(face)) {
                drawColoredQuad(wr, quad, r, g, b, a, tessellator);
            }
        }
        for (BakedQuad quad : model.getGeneralQuads()) {
            drawColoredQuad(wr, quad, r, g, b, a, tessellator);
        }
    }

    private static void drawColoredQuad(BufferBuilder wr, BakedQuad quad, float r, float g, float b, float a, Tessellator tessellator) {
        int[] vertexData = quad.getVertexData();
        final int vertexCount = 4;
        final int intsPerVertex = vertexData.length / vertexCount;

        wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < vertexCount; i++) {
            int baseIndex = i * intsPerVertex;
            float vx = Float.intBitsToFloat(vertexData[baseIndex]);
            float vy = Float.intBitsToFloat(vertexData[baseIndex + 1]);
            float vz = Float.intBitsToFloat(vertexData[baseIndex + 2]);

            wr.pos(vx, vy, vz).color(r, g, b, a).next();
        }
        BufferRenderer.drawWithGlobalProgram(wr.end());
    }

    public static void drawTracerLine(Entity e, int color, float lineWidth, float partialTicks) {
        if (e == null || mc.getEntityRenderDispatcher() == null) {
            return;
        }

        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) {
            viewEntity = mc.player;
        }
        if (viewEntity == null) {
            return;
        }

        double targetX = e.prevX + (e.getX() - e.prevX) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().x;
        double targetY = e.prevY + (e.getY() - e.prevY) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().y
                + (double) e.getEyeHeight() + (e.isSneaking() ? -0.125D : 0.0D);
        double targetZ = e.prevZ + (e.getZ() - e.prevZ) * (double) partialTicks - mc.getEntityRenderDispatcher().camera.getPos().z;

        double startX = 0.0D;
        double startY = viewEntity.getEyeHeight();
        double startZ = 0.0D;
        if (viewEntity == mc.player && mc.options.thirdPersonView == 0) {
            float yaw = viewEntity.rotationYaw;
            float pitch = viewEntity.rotationPitch;
            double dirX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            double dirY = -Math.sin(Math.toRadians(pitch));
            double dirZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            startX = dirX;
            startY += dirY;
            startZ = dirZ;
        }

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.setShaderColor(r, g, b, a);
        GL11.glVertex3d(startX, startY, startZ);
        GL11.glVertex3d(targetX, targetY, targetZ);
        RenderSystem.lineWidth(1.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        int j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlpha();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.pos((double) right, (double) top, 0.0D).color(f1, f2, f3, f).next();
        bufferBuilder.pos((double) left, (double) top, 0.0D).color(f1, f2, f3, f).next();
        bufferBuilder.pos((double) left, (double) bottom, 0.0D).color(f5, f6, f7, f4).next();
        bufferBuilder.pos((double) right, (double) bottom, 0.0D).color(f5, f6, f7, f4).next();
        BufferRenderer.drawWithGlobalProgram(wr.end());
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlpha();
        RenderSystem.enableTexture();
    }

    public static void db(int w, int h, int r) {
        int c = r == -1 ? -1089466352 : r;
        DrawContextHelper.drawRect(0, 0, w, h, c);
    }

    public static void drawColoredString(String text, char lineSplit, int x, int y, long s, long shift, boolean rect, TextRenderer fontRenderer) {
        int bX = x;
        int l = 0;
        long r = 0L;

        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == lineSplit) {
                ++l;
                x = bX;
                y += fontRenderer.FONT_HEIGHT + 5;
                r = shift * (long) l;
            }
            else {
                fontRenderer.draw(String.valueOf(c), (float) x, (float) y, Utils.getChroma(s, r), rect);
                x += fontRenderer.getCharWidth(c);
                if (c != ' ') {
                    r -= 90L;
                }
            }
        }

    }

    public static void drawCircle(double x, double y, double z, double radius, int sides, float lineWidth, int color, boolean chroma) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        mc.entityRenderer.disableLightmap();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(lineWidth);
        if (!chroma) {
            RenderSystem.setShaderColor(r, g, b, a);
        }

        long d = 0L;
        long ed = 15000L / (long) sides;
        long hed = ed / 2L;

        for (int i = 0; i < sides * 2; ++i) {
            if (chroma) {
                if (i % 2 != 0) {
                    if (i == 47) {
                        d = hed;
                    }

                    d += ed;
                }

                int c = Utils.getChroma(2L, d);
                float r2 = (float) (c >> 16 & 255) / 255.0F;
                float g2 = (float) (c >> 8 & 255) / 255.0F;
                float b2 = (float) (c & 255) / 255.0F;
                GL11.glColor3f(r2, g2, b2);
            }

            double angle = 6.283185307179586D * (double) i / (double) sides + Math.toRadians(180.0D);
            GL11.glVertex3d(x + Math.cos(angle) * radius, y, z + Math.sin(angle) * radius);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend(2848);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        mc.entityRenderer.enableLightmap();
    }

    public static void drawCaret(float x, float y, int color, double width, double length) {
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.disableBlend();
        RenderUtils.glColor(color);
        RenderSystem.lineWidth((float) width);
        float halfWidth = (float) (width / 2.0);
        float xOffset = halfWidth / 2.0f;
        float yOffset = halfWidth / 2.0f;
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawTriangle(double x, double y, double size, double widthDiv, double heightDiv, int color) {
        boolean blend = // GL11.isEnabled replaced(3042);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.getModelViewStack().pushMatrix();
        glColor(color);
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 0.8f);
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.enableTexture();
        if (!blend) {
            RenderSystem.disableBlend();
        }
        RenderSystem.disableBlend(2848);
    }

    public static void glColor(final int n) { // credit to the creator of raven b4
        RenderSystem.setShaderColor((float) (n >> 16 & 0xFF) / 255.0f, (float) (n >> 8 & 0xFF) / 255.0f, (float) (n & 0xFF) / 255.0f, (float) (n >> 24 & 0xFF) / 255.0f);
    }

    public static void drawRoundedGradientOutlinedRectangle(float x, float y, float x2, float y2, final float radius, final int n6, final int n7, final int n8) { // credit to the creator of raven b4
        x *= 2.0f;
        y *= 2.0f;
        x2 *= 2.0f;
        y2 *= 2.0f;
        RenderSystem.getModelViewStack().pushMatrix();
        GL11.glPushAttrib(// // GL11 constant);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        glColor(n6);
        for (int i = 0; i <= 90; i += 3) {
            final double n9 = (double) (i * 0.017453292f);
        }
        for (int j = 90; j <= 180; j += 3) {
            final double n10 = (double) (j * 0.017453292f);
        }
        for (int k = 0; k <= 90; k += 3) {
            final double n11 = (double) (k * 0.017453292f);
        }
        for (int l = 90; l <= 180; l += 3) {
            final double n12 = (double) (l * 0.017453292f);
        }
        RenderSystem.getModelViewStack().pushMatrix();
        GL11.glShadeModel(7425);
        RenderSystem.lineWidth(2.0f);
        if (n7 != 0L) {
            glColor(n7);
        }
        for (int n13 = 0; n13 <= 90; n13 += 3) {
            final double n14 = (double) (n13 * 0.017453292f);
        }
        for (int n15 = 90; n15 <= 180; n15 += 3) {
            final double n16 = (double) (n15 * 0.017453292f);
        }
        if (n8 != 0) {
            glColor(n8);
        }
        for (int n17 = 0; n17 <= 90; n17 += 3) {
            final double n18 = (double) (n17 * 0.017453292f);
        }
        for (int n19 = 90; n19 <= 180; n19 += 3) {
            final double n20 = (double) (n19 * 0.017453292f);
        }
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend(2848);
        RenderSystem.enableTexture();
        GL11.glPopAttrib();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.lineWidth(1.0f);
        GL11.glShadeModel(7424);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void draw2DPolygon(final double x, final double y, final double radius, final int sides, final int color) {
        if (sides < 3) {
            return;
        }
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, a);
        final double rad180 = Math.toRadians(180.0);
        bufferBuilder.begin(6, VertexFormats.POSITION);
        for (int i = 0; i < sides; ++i) {
            final double angle = 6.283185307179586 * i / sides + rad180;
            bufferBuilder.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0.0).next();
        }
        BufferRenderer.drawWithGlobalProgram(wr.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.getWindow().getFramebufferWidth() || framebuffer.framebufferHeight != mc.getWindow().getFramebufferHeight();
    }

    public static void drawFramebufferFullscreen(Framebuffer framebuffer) {
        if (framebuffer == null) return;
        ScaledResolution sr = new ScaledResolution(mc);
        RenderSystem.bindTexture(framebuffer.framebufferTexture);
        GL11.glTexCoord2d(0.0, 1.0);
        GL11.glTexCoord2d(0.0, 0.0);
        GL11.glTexCoord2d(1.0, 0.0);
        GL11.glTexCoord2d(1.0, 1.0);
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void setAlphaLimit(float limit) {
        RenderSystem.enableAlpha();
        RenderSystem.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static void resetColor() {
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static Vec3d convertTo2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(// // GL11 constant, MODELVIEW);
        GL11.glGetFloat(// // GL11 constant, PROJECTION);

        boolean result = GLU.gluProject(
                (float) x,
                (float) y,
                (float) z,
                MODELVIEW,
                PROJECTION,
                VIEWPORT,
                SCREEN_COORDS
        );

        if (result) {
            return new Vec3d(SCREEN_COORDS.get(0) / scaleFactor, (Display.getHeight() - SCREEN_COORDS.get(1)) / scaleFactor, SCREEN_COORDS.get(2));
        }

        return null;
    }

    public static ProjectionContext captureProjectionContext(ProjectionContext context, int scaleFactor) {
        if (context == null) {
            context = new ProjectionContext();
        }

        context.scaleFactor = scaleFactor;
        context.modelView.clear();
        context.projection.clear();
        context.viewport.clear();
        GL11.glGetFloat(// // GL11 constant, context.modelView);
        GL11.glGetFloat(// // GL11 constant, context.projection);
        context.projection.rewind();
        context.viewport.rewind();
        return context;
    }

    public static boolean projectTo2D(ProjectionContext context, double x, double y, double z, double[] output) {
        if (context == null || output == null || output.length < 3) {
            return false;
        }

        context.screenCoords.clear();
        boolean result = GLU.gluProject(
                (float) x,
                (float) y,
                (float) z,
                context.modelView,
                context.projection,
                context.viewport,
                context.screenCoords
        );

        if (!result) {
            return false;
        }

        output[0] = context.screenCoords.get(0) / context.scaleFactor;
        output[1] = (Display.getHeight() - context.screenCoords.get(1)) / context.scaleFactor;
        output[2] = context.screenCoords.get(2);
        return true;
    }

    public static void drawRoundedRectangle(float x, float y, float x2, float y2, float radius, final int color) {
        if (x2 <= x) {
            return;
        }

        float width = x2 - x;

        if (width < 3) {
            radius = Math.min(radius, width / 2.0f);
        }

        x *= 2.0;
        y *= 2.0;
        x2 *= 2.0;
        y2 *= 2.0;
        RenderSystem.getModelViewStack().pushMatrix();
        GL11.glPushAttrib(// // GL11 constant);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        glColor(color);
        for (int i = 0; i <= 90; i += 3) {
            final double n7 = (double) (i * 0.017453292f);
        }
        for (int j = 90; j <= 180; j += 3) {
            final double n8 = (double) (j * 0.017453292f);
        }
        if (x2 - x >= 4.5) {
            for (int k = 0; k <= 90; k += 1) {
                final double n9 = (double) (k * 0.017453292f);
            }
            for (int l = 90; l <= 180; l += 1) {
                final double n10 = (double) (l * 0.017453292f);
            }
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend(2848);
        RenderSystem.enableTexture();
        GL11.glPopAttrib();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRectangleGL(float x, float y, float x2, float y2, final int color) {
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableBlend();

        glColor(color);

        RenderSystem.enableBlend();
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawRoundedGradientRect(float x, float y, float x2, float y2, float radius, final int n6, final int n7, final int n8, final int n9) {
        if (x2 <= x) {
            return;
        }

        float width = x2 - x;

        if (width < 3) {
            radius = Math.min(radius, width / 2.0f);
        }

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFunc(770, 771);
        GL11.glShadeModel(7425);
        RenderSystem.getModelViewStack().pushMatrix();
        GL11.glPushAttrib(// // GL11 constant);
        x *= 2.0;
        y *= 2.0;
        x2 *= 2.0;
        y2 *= 2.0;
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        glColor(n6);
        GL11.glShadeModel(7425);
        for (int i = 0; i <= 90; i += 3) {
            final double n10 = i * 0.017453292f;
        }
        glColor(n7);
        for (int j = 90; j <= 180; j += 3) {
            final double n11 = j * 0.017453292f;
        }
        if (x2 - x >= 4.5) {
            glColor(n8);
            for (int k = 0; k <= 90; k += 3) {
                final double n12 = k * 0.017453292f;
            }
            glColor(n9);
            for (int l = 90; l <= 180; l += 3) {
                final double n13 = l * 0.017453292f;
            }
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend(2848);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        GL11.glPopAttrib();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend(2848);
        GL11.glShadeModel(7424);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static int setAlpha(int rgb, double alpha) {
        if (alpha < 0 || alpha > 1) {
            alpha = 0.5;
        }

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        int alphaInt = (int) (alpha * 255);

        int rgba = (alphaInt << 24) | (red << 16) | (green << 8) | blue;

        return rgba;
    }

    public static void draw2DCircle(float centerX, float centerY, float radius, int segments,
                                    float lineWidth, float r, float g, float b, float a) {
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.lineWidth(lineWidth);

        for (int i = 0; i <= segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            float x = (float) (radius * Math.cos(theta)) + centerX;
            float y = (float) (radius * Math.sin(theta)) + centerY;
        }

        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.lineWidth(1);
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void draw2DCircleArc(float centerX, float centerY, float radius,
                                       float startAngle, float endAngle, float lineWidth, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.lineWidth(lineWidth);

        for (float angle = startAngle; angle <= endAngle; angle += 1) {
            double theta = Math.toRadians(angle + 180);
            float x = (float) (radius * Math.cos(theta)) + centerX;
            float y = (float) (radius * Math.sin(theta)) + centerY;
        }

        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.lineWidth(1);
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void drawHorizontalGradientRect(float left, float top, float right, float bottom, int leftColor, int rightColor) {
        float la = (leftColor >> 24 & 0xFF) / 255.0F;
        float lr = (leftColor >> 16 & 0xFF) / 255.0F;
        float lg = (leftColor >> 8 & 0xFF) / 255.0F;
        float lb = (leftColor & 0xFF) / 255.0F;
        float ra = (rightColor >> 24 & 0xFF) / 255.0F;
        float rr = (rightColor >> 16 & 0xFF) / 255.0F;
        float rg = (rightColor >> 8 & 0xFF) / 255.0F;
        float rb = (rightColor & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlpha();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        wr.pos(left, bottom, 0).color(lr, lg, lb, la).next();
        wr.pos(right, bottom, 0).color(rr, rg, rb, ra).next();
        wr.pos(right, top, 0).color(rr, rg, rb, ra).next();
        wr.pos(left, top, 0).color(lr, lg, lb, la).next();
        BufferRenderer.drawWithGlobalProgram(wr.end());
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlpha();
        RenderSystem.enableTexture();
    }

    public static void drawVerticalGradientRect(float left, float top, float right, float bottom, int topColor, int bottomColor) {
        float ta = (topColor >> 24 & 0xFF) / 255.0F;
        float tr = (topColor >> 16 & 0xFF) / 255.0F;
        float tg = (topColor >> 8 & 0xFF) / 255.0F;
        float tb = (topColor & 0xFF) / 255.0F;
        float ba = (bottomColor >> 24 & 0xFF) / 255.0F;
        float br = (bottomColor >> 16 & 0xFF) / 255.0F;
        float bg = (bottomColor >> 8 & 0xFF) / 255.0F;
        float bb = (bottomColor & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlpha();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        wr.pos(right, top, 0).color(tr, tg, tb, ta).next();
        wr.pos(left, top, 0).color(tr, tg, tb, ta).next();
        wr.pos(left, bottom, 0).color(br, bg, bb, ba).next();
        wr.pos(right, bottom, 0).color(br, bg, bb, ba).next();
        BufferRenderer.drawWithGlobalProgram(wr.end());
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlpha();
        RenderSystem.enableTexture();
    }

    public static void renderItemAndEffectIntoGui3D(ItemStack stack, int xPos, int yPos) {
        if (stack == null) return;

        RenderSystem.getModelViewStack().pushMatrix();
        prepareGuiItemRenderState();
        RenderSystem.depthMask(true);
        RenderSystem.clear(// // GL11 constant);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.scale(1.0f, 1.0f, -0.01f);
        mc.getRenderItem().zLevel = -150.0f;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
        mc.getRenderItem().zLevel = 0.0f;
        RenderSystem.getModelViewStack().popMatrix();
        RenderHelper.disableStandardItemLighting();
        prepareGuiTextureRenderState();
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static void renderItemAndEffectIntoGui2D(ItemStack stack, int xPos, int yPos) {
        if (stack == null) return;

        prepareGuiItemRenderState();
        mc.getRenderItem().zLevel = -150.0F;
        RenderSystem.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos - 8);
        mc.getRenderItem().zLevel = 0.0F;
        RenderSystem.disableDepth();
        prepareGuiTextureRenderState();
        RenderSystem.disableBlend();
    }

    public static int getDurabilityColor(float ratio) {
        if (ratio > 0.6F) return 0x00FF00;
        if (ratio > 0.3F) return 0xFFFF00;
        return 0xFF0000;
    }

    public static void drawDurabilityBar(int xPos, int yPos, float durabilityRatio) {
        int barWidth = (int) (durabilityRatio * 13);
        int barColor = getDurabilityColor(durabilityRatio);

        RenderSystem.disableTexture();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder wr = tess.getBuffer();

        wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        wr.pos(xPos + 2, yPos + 15, 0).color(0.0F, 0.0F, 0.0F, 1.0F).next();
        wr.pos(xPos + 2, yPos + 16, 0).color(0.0F, 0.0F, 0.0F, 1.0F).next();
        wr.pos(xPos + 15, yPos + 16, 0).color(0.0F, 0.0F, 0.0F, 1.0F).next();
        wr.pos(xPos + 15, yPos + 15, 0).color(0.0F, 0.0F, 0.0F, 1.0F).next();
        tess.draw();

        float r = ((barColor >> 16) & 255) / 255.0F;
        float g = ((barColor >> 8) & 255) / 255.0F;
        float b = (barColor & 255) / 255.0F;
        wr.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        wr.pos(xPos + 2, yPos + 15, 0).color(r, g, b, 1.0F).next();
        wr.pos(xPos + 2, yPos + 16, 0).color(r, g, b, 1.0F).next();
        wr.pos(xPos + 2 + barWidth, yPos + 16, 0).color(r, g, b, 1.0F).next();
        wr.pos(xPos + 2 + barWidth, yPos + 15, 0).color(r, g, b, 1.0F).next();
        tess.draw();

        RenderSystem.enableTexture();
    }

    public static int getEnchantColor(int level) {
        switch (level) {
            case 1: return 0xFFFFFF;
            case 2: return 0x55FFFF;
            case 3: return 0x00AAAA;
            case 4: return 0xAA00AA;
            case 5: return 0xFFAA00;
            case 10: return 0xFF55FF;
            default: return level > 5 ? 0xFF55FF : 0xFFFFFF;
        }
    }

    public static int drawEnchantWithColor(TextRenderer fr, String letter, int level, int x, int y) {
        int letterWidth = fr.drawStringWithShadow(letter, x, y, 0xFFFFFF);
        fr.drawStringWithShadow(String.valueOf(level), letterWidth, y, getEnchantColor(level));
        return letterWidth;
    }

    public static void prepareGuiTextureRenderState() {
        RenderSystem.disableLighting();
        RenderSystem.disableDepth();
        RenderSystem.depthMask(false);
        RenderSystem.enableTexture();
        RenderSystem.enableAlpha();
        RenderSystem.alphaFunc(// // GL11 constant, 0.1F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(// // GL11 constant, // // GL11 constant, // // GL11 constant, // // GL11 constant);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void prepareGuiItemRenderState() {
        RenderSystem.disableLighting();
        RenderSystem.enableTexture();
        RenderSystem.enableAlpha();
        RenderSystem.alphaFunc(// // GL11 constant, 0.1F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(// // GL11 constant, // // GL11 constant, // // GL11 constant, // // GL11 constant);
        RenderSystem.enableDepth();
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static String getEnchantmentAbbreviated(int id) {
        switch (id) {
            case 0: return "pt";
            case 1: return "frp";
            case 2: return "ff";
            case 3: return "blp";
            case 4: return "prp";
            case 5: return "thr";
            case 6: return "res";
            case 7: return "aa";
            case 16: return "sh";
            case 17: return "smt";
            case 18: return "ban";
            case 19: return "kb";
            case 20: return "fa";
            case 21: return "lot";
            case 32: return "eff";
            case 33: return "sil";
            case 34: return "ub";
            case 35: return "for";
            case 48: return "pow";
            case 49: return "pun";
            case 50: return "flm";
            case 51: return "inf";
            default: return null;
        }
    }

    public static Identifier buildWhiteMaskedTexture(String resourcePath, String registryName, Identifier fallback) {
        try (InputStream stream = Raven.class.getResourceAsStream(resourcePath)) {
            if (stream == null) return fallback;
            BufferedImage src = ImageIO.read(stream);
            int w = src.getWidth(), h = src.getHeight();
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int py = 0; py < h; py++) {
                for (int px = 0; px < w; px++) {
                    int alpha = (src.getRGB(px, py) >>> 24) & 0xFF;
                    if (alpha > 0) dst.setRGB(px, py, (alpha << 24) | 0x00FFFFFF);
                }
            }
            return mc.getTextureManager().getDynamicTextureLocation(registryName, new DynamicTexture(dst));
        } catch (Exception e) {
            e.printStackTrace();
            return fallback;
        }
    }

    private static final java.util.Map<String, Identifier> iconCache = new java.util.HashMap<>();

    /**
     * Returns a cached white-masked icon texture, loading it on first access.
     * The resource path should start with "/" (e.g. "/assets/keystrokesmod/textures/gui/close.png").
     */
    public static Identifier getIcon(String resourcePath) {
        Identifier cached = iconCache.get(resourcePath);
        if (cached != null) {
            return cached;
        }
        String registryName = "raven_icon_" + resourcePath.hashCode();
        Identifier icon = buildWhiteMaskedTexture(resourcePath, registryName, null);
        if (icon != null) {
            iconCache.put(resourcePath, icon);
        }
        return icon;
    }

    /**
     * Draws a tinted icon texture at the given position with full GL state management.
     * Saves and restores depth/blend state automatically.
     */
    public static void drawIcon(Identifier texture, float x, float y, int size, int argbColor) {
        if (texture == null) {
            return;
        }
        boolean depthEnabled = // GL11.isEnabled replaced(// // GL11 constant);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        prepareGuiTextureRenderState();
        mc.getTextureManager().bindTexture(texture);
        float a = ((argbColor >>> 24) & 0xFF) / 255f;
        float r = ((argbColor >> 16) & 0xFF) / 255f;
        float g = ((argbColor >> 8) & 0xFF) / 255f;
        float b = (argbColor & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.translate(x, y, 0f);
        net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, size, size, size, size);
        RenderSystem.getModelViewStack().popMatrix();

        restoreGuiRenderState(depthEnabled, blendEnabled, depthMask);
    }

    public static void restoreGuiRenderState(boolean depthEnabled, boolean blendEnabled, boolean depthMask) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (blendEnabled) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }
        if (depthEnabled) {
            RenderSystem.enableDepth();
        } else {
            RenderSystem.disableDepth();
        }
        RenderSystem.depthMask(depthMask);
    }
}
