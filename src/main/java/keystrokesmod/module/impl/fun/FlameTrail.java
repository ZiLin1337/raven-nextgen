package keystrokesmod.module.impl.fun;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.particle.ParticleTypes;

public class FlameTrail extends Module {
    public FlameTrail() { super("FlameTrail", category.fun); }

    public void onUpdate() {
        if (mc.player != null && mc.world != null && mc.player.age % 2 == 0) {
            mc.world.addParticle(ParticleTypes.FLAME, mc.player.getX(), mc.player.getY(), mc.player.getZ(), 0, 0, 0);
        }
    }
}
