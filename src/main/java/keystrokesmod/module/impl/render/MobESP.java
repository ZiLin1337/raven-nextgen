package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import net.minecraft.entity.LivingEntity;

public class MobESP extends Module {
    public static boolean renderingOutlinePass = false;

    public MobESP() {
        super("MobESP", category.render);
    }

    public static void onRenderMobPre(LivingEntity entity) {
    }

    public static void onRenderMobPost() {
    }
}