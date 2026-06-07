package keystrokesmod.module.impl.render;

import keystrokesmod.event.impl.RenderEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerESP extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ButtonSetting showSelf;
    private final SliderSetting range;
    
    public PlayerESP() {
        super("Player ESP", category.render);
        this.registerSetting(showSelf = new ButtonSetting("Show self", false));
        this.registerSetting(range = new SliderSetting("Range", 64, 16, 256, 1));
    }
    
    public void onRender(RenderEvent event) {
        if (mc.world == null || mc.player == null) return;
        
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player) {
                if (!showSelf.isToggled() && player == mc.player) continue;
                if (mc.player.distanceTo(player) > range.getInput()) continue;
                
                Vec3d pos = player.getPos().subtract(cameraPos);
                RenderUtils.drawPlayerBoundingBox(pos, 0xFF00FF00); // Green
            }
        }
    }
}
