package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import meteordevelopment.orbit.EventHandler;

public class AntiShuffle extends Module {
    public AntiShuffle() { super("AntiShuffle", category.render); }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player == null) return;
        // Prevent item shuffling animation
    }
}
