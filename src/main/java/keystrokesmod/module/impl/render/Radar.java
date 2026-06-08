package keystrokesmod.module.impl.render;

import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.event.TickEvent;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.Gui;

import net.minecraft.entity.player.PlayerEntity;


import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Radar extends Module {
    private ButtonSetting tracerLines;
    
    private int scale = 2;
    
    private static final int RECT_COLOR = new Color(0, 0, 0, 125).getRGB();
    
    public Radar() {
        super("Radar", category.render);
        this.registerSetting(tracerLines = new ButtonSetting("Show tracer lines", false));
    }

    @Override
    public void onUpdate() {
        this.scale = /* Window removed in 1.21.4 */ null.getScaleFactor();
    }

    
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen instanceof ClickGui) {
            return;
        }
        if (mc.currentScreen != null || mc.options.showDebugInfo) {
            return;
        }
        int x = 5;
        int y = 70;
        int rightX = x + 100;
        int bottomY = y + 100;
        DrawContextHelper.DrawContextHelper.drawRect(x, y, rightX, bottomY, RECT_COLOR);
        DrawContextHelper.DrawContextHelper.drawRect(x - 1, y - 1, rightX + 1, y, -1);
        DrawContextHelper.DrawContextHelper.drawRect(x - 1, bottomY, rightX + 1, bottomY + 1, -1);
        DrawContextHelper.DrawContextHelper.drawRect(x - 1, y, x, bottomY, -1);
        DrawContextHelper.DrawContextHelper.drawRect(rightX, y, rightX + 1, bottomY, -1);
        int playerIndicatorX = rightX / 2 + 3;
        int playerIndicatorY = y + 52;
        RenderUtils.drawPolygon(playerIndicatorX, playerIndicatorY, 5.0, 3, -1);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * this.scale, mc.getWindow().getFramebufferHeight() - this.scale * 170, rightX * this.scale - this.scale * 5, this.scale * 100);
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && player.deathTime == 0) {
                if (AntiBot.isBot(player)) {
                    continue;
                }
                double distanceSquared = player.getDistanceSqToEntity(mc.player);
                if (distanceSquared > 360.0) {
                    continue;
                }
                double playerAngle = (mc.player.rotationYaw + Math.atan2(player.posX - mc.player.getX(), player.posZ - mc.player.getZ()) * 57.295780181884766) % 360.0;
                double scaledDistance = distanceSquared / 5.0;
                double xOffset = scaledDistance * Math.sin(Math.toRadians(playerAngle));
                double zOffset = scaledDistance * Math.cos(Math.toRadians(playerAngle));
                if (tracerLines.isToggled()) {
                    RenderSystem.getModelViewStack().pushMatrix();
                    RenderSystem.enableBlend(GL11.GL_BLEND);
                    RenderSystem.enableBlend(GL11.GL_LINE_SMOOTH);
                    RenderSystem.disableBlend(GL11.GL_DEPTH_TEST);
                    RenderSystem.disableBlend(GL11.GL_TEXTURE_2D);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    RenderSystem.enableBlend(GL11.GL_BLEND);
                    RenderSystem.lineWidth(0.5f);
                    GL11.glColor3d(1.0, 1.0, 1.0);
                    // GL11 replaced(GL11.GL_LINES);
                    // GL11(playerIndicatorX, playerIndicatorY);
                    // GL11((double)playerIndicatorX - xOffset, (double)playerIndicatorY - zOffset);
                    // GL11 replaced();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.disableBlend(GL11.GL_BLEND);
                    RenderSystem.enableBlend(GL11.GL_TEXTURE_2D);
                    RenderSystem.enableBlend(GL11.GL_DEPTH_TEST);
                    RenderSystem.disableBlend(GL11.GL_LINE_SMOOTH);
                    RenderSystem.disableBlend(GL11.GL_BLEND);
                    RenderSystem.getModelViewStack().popMatrix();
                }
                RenderUtils.drawPolygon((double)playerIndicatorX - xOffset, (double)playerIndicatorY - zOffset, 3.0, 4, Color.red.getRGB());
            }
        }
        RenderSystem.disableBlend(GL11.GL_SCISSOR_TEST);
        RenderSystem.getModelViewStack().popMatrix();
    }
}
