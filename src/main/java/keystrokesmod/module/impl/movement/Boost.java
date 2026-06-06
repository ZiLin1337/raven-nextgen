package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class Boost extends Module {
    private SliderSetting speed;
    public Boost() {
        super("Boost", category.movement);
        registerSetting(speed = new SliderSetting("Speed", 1.5, 1.0, 5.0, 0.1));
    }
    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || mc.player == null) return;
        if (mc.player.isOnGround() || mc.player.fallDistance < 0.5f) return;
        mc.player.setVelocity(mc.player.getVelocity().multiply(speed.getInput()));
    }
}
