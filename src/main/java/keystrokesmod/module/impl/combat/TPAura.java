package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.Comparator;

public class TPAura extends Module {
    private SliderSetting range, cps;
    private LivingEntity target;

    public TPAura() {
        super("TPAura", category.combat);
        this.registerSetting(range = new SliderSetting("Range", 6, 2, 15, 0.5));
        this.registerSetting(cps = new SliderSetting("CPS", 8, 1, 20, 1));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.world == null) return;
        target = mc.world.getEntitiesByClass(LivingEntity.class, mc.player.getBoundingBox().expand(range.getInput()),
                e2 -> e2 != mc.player && e2.isAlive() && e2 instanceof PlayerEntity)
                .stream().min(Comparator.comparingDouble(e2 -> mc.player.distanceTo(e2))).orElse(null);
        if (target == null) return;
        float[] rots = RotationUtils.getRotations(target);
        e.setYaw(rots[0]);
        e.setPitch(rots[1]);
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
