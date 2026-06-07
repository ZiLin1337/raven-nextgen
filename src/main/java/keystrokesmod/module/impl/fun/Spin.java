package keystrokesmod.module.impl.fun;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import meteordevelopment.orbit.EventHandler;

public class Spin extends Module {
    private SliderSetting speed;

    public Spin() {
        super("Spin", category.fun);
        this.registerSetting(speed = new SliderSetting("Speed", 10, 5, 50, 1));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player != null)) {
            mc.player.setYaw(mc.player.getYaw() + speed.getInput());
        }
    }
}
