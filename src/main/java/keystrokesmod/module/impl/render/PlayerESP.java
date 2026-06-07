package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.player.Freecam;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.GroupSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PlayerESP extends Module {
    public ColorSetting color;
    public ButtonSetting teamColor;
    public ButtonSetting rainbow;
    public GroupSetting espTypes;
    public ButtonSetting twoD;
    public ButtonSetting box;
    public ButtonSetting healthBar;
    public ButtonSetting outline;
    public ButtonSetting shaded;
    public ButtonSetting skeleton;
    public ButtonSetting ring;
    public ButtonSetting redOnDamage;
    public ButtonSetting renderSelf;
    public ButtonSetting showInvis;
    private final SliderSetting maxDistance;
    private static final float RAD_TO_DEG = 57.29578f;
    public static boolean renderingOutlinePass = false;

    public PlayerESP() {
        super("PlayerESP", category.render, 0);
        this.registerSetting(espTypes = new GroupSetting("Types"));
        this.registerSetting(twoD = new ButtonSetting(espTypes, "2D", false));
        this.registerSetting(box = new ButtonSetting(espTypes, "Box", true));
        this.registerSetting(outline = new ButtonSetting(espTypes, "Outline", false));
        this.registerSetting(ring = new ButtonSetting(espTypes, "Ring", false));
        this.registerSetting(shaded = new ButtonSetting(espTypes, "Shaded", false));
        this.registerSetting(skeleton = new ButtonSetting(espTypes, "Skeleton", false));
        this.registerSetting(color = new ColorSetting("Color", 0, 255, 0));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", false));
        this.registerSetting(healthBar = new ButtonSetting("Health bar", true));
        this.registerSetting(redOnDamage = new ButtonSetting("Red on damage", true));
        this.registerSetting(renderSelf = new ButtonSetting("Render self", false));
        this.registerSetting(teamColor = new ButtonSetting("Team color", false));
        this.registerSetting(showInvis = new ButtonSetting("Show invis", true));
        this.registerSetting(maxDistance = new SliderSetting("Max distance", 128.0, 32.0, 256.0, 8.0));
    }

    @Override
    public void guiUpdate() {
        twoD.setVisible(box.isToggled(), this);
    }

    public void onRender3D(net.minecraft.client.util.math.MatrixStack matrices, float partialTicks) {
        if (!Utils.nullCheck()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (!renderSelf.isToggled() && player == mc.player) continue;
            if (mc.player.distanceTo(player) > maxDistance.getInput()) continue;
            if (!showInvis.isToggled() && player.isInvisible()) continue;
            if (AntiBot.isBot(player)) continue;
            
            int espColor = color.getColor();
            if (rainbow.isToggled()) {
                espColor = java.awt.Color.HSBtoRGB((System.currentTimeMillis()%10000L)/10000.0f,0.8f,1.0f);
            }
            
            Vec3d pos = player.getPos().subtract(cameraPos);
            Box box_bb = player.getBoundingBox().offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            
            if (box.isToggled()) {
                RenderUtils.drawOutlinedBox();
            }
            if (outline.isToggled()) {
                // RenderUtils.drawOutline removed
            }
            if (healthBar.isToggled()) {
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float healthPercent = health / maxHealth;
                int healthColor = healthPercent > 0.6f ? 0xFF00FF00 : (healthPercent > 0.3f ? 0xFFFFFF00 : 0xFFFF0000);
                double healthHeight = (box_bb.maxY - box_bb.minY) * healthPercent;
                Box healthBox = new Box(box_bb.minX - 0.1, box_bb.minY, box_bb.minZ - 0.1,
                    box_bb.minX, box_bb.minY + healthHeight, box_bb.minZ);
                RenderUtils.drawBox(healthBox, healthColor);
            }
        }
    }
}
