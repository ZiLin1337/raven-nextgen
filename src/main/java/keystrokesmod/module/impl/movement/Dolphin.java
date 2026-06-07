package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class Dolphin extends Module {
    private SliderSetting speed;
    public Dolphin() {
        super("Dolphin", category.movement);
        registerSetting(speed = new SliderSetting("Speed", 1.0, 0.5, 3.0, 0.1));
    }
    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || mc.player == null) return;
        if (mc.player.isTouchingWater() && mc.options.jumpKey.isPressed() {
            mc.player.setVelocity(mc.player.getVelocity().add(0, speed.getInput() * 0.1, 0));
        }
    }
}
