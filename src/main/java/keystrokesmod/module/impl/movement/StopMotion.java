package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import meteordevelopment.orbit.EventHandler;

public class StopMotion extends Module {
    private ButtonSetting onDisableOnly;

    public StopMotion() {
        super("StopMotion", category.movement);
        this.registerSetting(onDisableOnly = new ButtonSetting("On disable only", false));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (onDisableOnly.isToggled()) return;
        if (mc.player != null) mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
    }

    public void onDisable() {
        if (mc.player != null) mc.player.setVelocity(0, 0, 0);
    }
}
