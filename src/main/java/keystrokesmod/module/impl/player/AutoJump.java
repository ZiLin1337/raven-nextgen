package keystrokesmod.module.impl.player;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;

public class AutoJump extends Module {
    public AutoJump() { super("AutoJump", category.player); }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player != null && mc.player.isOnGround() && Utils.isMoving()) {
            mc.player.jump();
        }
    }
}
