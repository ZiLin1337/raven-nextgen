package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import meteordevelopment.orbit.EventHandler;

public class MovementFix extends Module {
    public MovementFix() { super("MovementFix", category.movement); }

    @EventHandler
    public void onPrePlayerInput(PrePlayerInputEvent e) {
        if (mc.player == null) return;
        // Fix movement values when rotating silently
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        if (forward != 0 || strafe != 0) {
            float yaw = mc.player.getYaw();
            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            e.setForward(forward);
            e.setStrafe(strafe);
        }
    }
}
