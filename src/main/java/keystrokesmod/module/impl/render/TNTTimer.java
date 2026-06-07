package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3dd;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.awt.Color;

public class TNTTimer extends Module {
    private static final int BEDWARS_FUSE_OFFSET = 28;
    private static final float BEDWARS_MAX_FUSE = 52.0f;
    private static final float NORMAL_MAX_FUSE = 80.0f;

    private final SliderSetting scale;
    private final DecimalFormat timeFormatter = new DecimalFormat("0.0");
    private final ArrayList<TntEntity> trackedTnt = new ArrayList<>();
    private int trackedTntCount = 0;
    private boolean trackedBedwars = false;

    public TNTTimer() {
        super("TNTTimer", category.render);
        this.registerSetting(scale = new SliderSetting("Scale", 1.0, 0.5, 3.0, 0.1));
    }

    public void onUpdate() {
        updateTrackedTnt();
    }

    public void onRenderWorld(MatrixStack matrices) {
        if (!Utils.nullCheck() || trackedTntCount == 0) return;

        Vec3dd camPos = mc.gameRenderer.getCamera().getPos();
        TextRenderer fr = mc.textRenderer;

        for (int i = 0; i < trackedTntCount; i++) {
            TntEntity tnt = trackedTnt.get(i);
            if (tnt == null || tnt.isRemoved()) continue;

            int fuse = trackedBedwars ? (tnt.getFuse() - BEDWARS_FUSE_OFFSET) : tnt.getFuse();
            if (fuse < 1) continue;

            Vec3dd pos = tnt.getPos();
            double x = pos.x - camPos.x;
            double y = pos.y + tnt.getHeight() + 0.5 - camPos.y;
            double z = pos.z - camPos.z;

            renderTimer(matrices, fr, x, y, z, fuse);
        }
    }

    private void updateTrackedTnt() {
        trackedTntCount = 0;
        trackedBedwars = false;
        if (!Utils.nullCheck() || mc.world == null) return;

        trackedBedwars = Utils.getBedwarsStatus() != 0;
        for (TntEntity tnt : mc.world.getEntitiesByClass(TntEntity.class,
                mc.player.getBoundingBox().expand(64), e -> true) {
            int fuse = trackedBedwars ? (tnt.getFuse() - BEDWARS_FUSE_OFFSET) : tnt.getFuse();
            if (fuse < 1) continue;
            if (trackedTntCount >= trackedTnt.size()) trackedTnt.add(tnt);
            else trackedTnt.set(trackedTntCount, tnt);
            trackedTntCount++;
        }
    }

    private void renderTimer(MatrixStack matrices, TextRenderer fr, double x, double y, double z, int fuse) {
        String time = timeFormatter.format(fuse / 20.0f);
        float maxFuse = trackedBedwars ? BEDWARS_MAX_FUSE : NORMAL_MAX_FUSE;
        float green = Math.min(fuse / maxFuse, 1.0f);
        Color color = new Color(1.0f - green, green, 0.0f);

        matrices.push();
        matrices.translate(x, y, z);
        RenderSystem.disableDepthTest();

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int halfWidth = fr.getWidth(time) / 2;

        // Background
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buf.vertex(matrix, -halfWidth - 1, -1, 0).color(0, 0, 0, 128).next();
        buf.vertex(matrix, -halfWidth - 1, 8, 0).color(0, 0, 0, 128).next();
        buf.vertex(matrix, halfWidth + 1, 8, 0).color(0, 0, 0, 128).next();
        buf.vertex(matrix, halfWidth + 1, -1, 0).color(0, 0, 0, 128).next();
        BufferRenderer.drawWithGlobalProgram(buf.end());

        // Text
        fr.draw(matrices, time, -halfWidth, 0, color.getRGB());

        RenderSystem.enableDepthTest();
        matrices.pop();
    }
}
