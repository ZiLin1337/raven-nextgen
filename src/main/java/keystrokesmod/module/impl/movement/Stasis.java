package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import meteordevelopment.orbit.EventHandler;

public class Stasis extends Module {
    private ButtonSetting freezeX, freezeY, freezeZ;
    private double savedX, savedY, savedZ;

    public Stasis() {
        super("Stasis", category.movement);
        this.registerSetting(freezeX = new ButtonSetting("Freeze X", true));
        this.registerSetting(freezeY = new ButtonSetting("Freeze Y", false));
        this.registerSetting(freezeZ = new ButtonSetting("Freeze Z", true));
    }

    public void onEnable() {
        if (mc.player != null) {
            savedX = mc.player.getX();
            savedY = mc.player.getY();
            savedZ = mc.player.getZ();
        }
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player == null) return;
        double x = freezeX.isToggled() ? savedX : mc.player.getX();
        double y = freezeY.isToggled() ? savedY : mc.player.getY();
        double z = freezeZ.isToggled() ? savedZ : mc.player.getZ();
        mc.player.setVelocity(0, 0, 0);
        mc.player.setPosition(x, y, z);
    }
}
