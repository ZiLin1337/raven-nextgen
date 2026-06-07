package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class KeepSprint extends Module {
    private SliderSetting motion;

    public KeepSprint() {
        super("KeepSprint", category.movement);
        this.registerSetting(motion = new SliderSetting("Motion", 0.8, 0.1, 1.0, 0.05));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player != null && KillAura.target != null && mc.player.hurtTime > 0)) {
            mc.player.setVelocity(mc.player.getVelocity().x * motion.getInput(), mc.player.getVelocity().y, mc.player.getVelocity().z * motion.getInput());
        }
    }
}
