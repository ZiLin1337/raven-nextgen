package keystrokesmod.module.impl.render;

import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import java.util.*;

public class BlockESP extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final SliderSetting range, mode;
    private final ButtonSetting showOres, showChests, showSpawners, showOutline, showFill;
    private final SliderSetting colorR, colorG, colorB, colorA;
    private final BlockListSetting targetBlocks;

    public BlockESP() {
        super("BlockESP", category.render);
        registerSetting(range = new SliderSetting("Range", 50, 10, 200, 1));
        registerSetting(mode = new SliderSetting("Mode", 0, 0, 2, 1));
        registerSetting(showOres = new ButtonSetting("Show ores", true));
        registerSetting(showChests = new ButtonSetting("Show chests", true));
        registerSetting(showSpawners = new ButtonSetting("Show spawners", true));
        registerSetting(showOutline = new ButtonSetting("Show outline", true));
        registerSetting(showFill = new ButtonSetting("Show fill", true));
        registerSetting(colorR = new SliderSetting("Color R", 255, 0, 255, 1));
        registerSetting(colorG = new SliderSetting("Color G", 255, 0, 255, 1));
        registerSetting(colorB = new SliderSetting("Color B", 0, 0, 255, 1));
        registerSetting(colorA = new SliderSetting("Color A", 100, 0, 255, 1));
        registerSetting(targetBlocks = new BlockListSetting("Target blocks"));
    }

    @EventHandler
    public void onRender3D(Render3DEvent e) {
        if (!Utils.nullCheck() || mc.player == null || mc.world == null) return;
        int rangeInt = (int) range.getInput();
        BlockPos playerPos = mc.player.getBlockPos();
        int color = ((int) colorA.getInput() << 24) | ((int) colorR.getInput() << 16) | ((int) colorG.getInput() << 8) | (int) colorB.getInput();
        for (BlockPos pos : BlockPos.iterate(playerPos.add(-rangeInt, -rangeInt, -rangeInt), playerPos.add(rangeInt, rangeInt, rangeInt)) {
            BlockState state = mc.world.getBlockState(pos);
            Block block = state.getBlock();
            if (shouldRender(block) {
                renderBlock(pos, color);
            }
        }
    }

    private boolean shouldRender(Block block) {
        if (showOres.isToggled() && isOre(block)) return true;
        if (showChests.isToggled() && isChest(block)) return true;
        if (showSpawners.isToggled() && isSpawner(block)) return true;
        return targetBlocks != null && targetBlocks.contains(block);
    }

    private boolean isOre(Block block) {
        String name = Registries.BLOCK.getId(block).getPath();
        return name.contains("_ore");
    }

    private boolean isChest(Block block) {
        return block instanceof net.minecraft.block.ChestBlock || block instanceof net.minecraft.block.EnderChestBlock;
    }

    private boolean isSpawner(Block block) {
        return block instanceof net.minecraft.block.SpawnerBlock;
    }

    private void renderBlock(BlockPos pos, int color) {
        double x = pos.getX() - mc.getEntityRenderDispatcher().camera.getPos().x;
        double y = pos.getY() - mc.getEntityRenderDispatcher().camera.getPos().y;
        double z = pos.getZ() - mc.getEntityRenderDispatcher().camera.getPos().z;
        Box box = new Box(x, y, z, x + 1, y + 1, z + 1);
        if (showFill.isToggled()) drawFilledBox(box, color);
        if (showOutline.isToggled()) drawOutlinedBox(box, color | 0xFF000000);
    }

    private void drawFilledBox(Box box, int color) {
        float a = (float) (color >> 24 & 0xFF) / 255.0f;
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;
        RenderSystem.enableBlend(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend(GL11.GL_TEXTURE_2D);
        RenderSystem.disableBlend(GL11.GL_CULL_FACE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableBlend(GL11.GL_CULL_FACE);
        RenderSystem.enableBlend(GL11.GL_TEXTURE_2D);
        RenderSystem.disableBlend(GL11.GL_BLEND);
    }

    private void drawOutlinedBox(Box box, int color) {
        float a = (float) (color >> 24 & 0xFF) / 255.0f;
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;
        RenderSystem.enableBlend(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend(GL11.GL_TEXTURE_2D);
        RenderSystem.disableBlend(GL11.GL_DEPTH_TEST);
        RenderSystem.lineWidth(2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
        buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableBlend(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend(GL11.GL_TEXTURE_2D);
        RenderSystem.disableBlend(GL11.GL_BLEND);
    }
}
