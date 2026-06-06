package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class ESP extends Module {
    private SliderSetting mode;

    public ESP() {
        super("ESP", category.render);
        this.registerSetting(mode = new SliderSetting("Mode", 0, new String[]{"Box", "2D", "Mixed"}));
    }

    public void onRenderWorld(Object MatrixStack matrices) {
        if (mc.world == null) return;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isDead()) continue;
            Box bb = p.getBoundingBox();
            RenderUtils.drawESPBox(matrices, p.getBlockPos(), 255, 255, 255, 100);
        }
    }
}
