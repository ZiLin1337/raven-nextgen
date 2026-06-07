package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import meteordevelopment.orbit.EventHandler;

public class InstantStop extends Module {
    private ButtonSetting stopOnRelease;

    public InstantStop() {
        super("InstantStop", category.movement);
        this.registerSetting(stopOnRelease = new ButtonSetting("Stop on release", true));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player == null) return;
        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0)) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }
    }
}
