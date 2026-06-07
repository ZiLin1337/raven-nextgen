package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class JumpReset extends Module {
    private SliderSetting mode;
    private SliderSetting delay;
    private String[] modes = {"OnDamage", "Always"};
    private boolean shouldReset;

    public JumpReset() {
        super("Jump Reset", category.combat);
        this.registerSetting(mode = new SliderSetting("Mode", 0, modes));
        this.registerSetting(delay = new SliderSetting("Delay", "ms", 0, 0, 200, 10));
    }

    @Override
    public void onEnable() { shouldReset = false; }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || mc.player == null) return;
        if (!shouldReset) return;

        if (mc.player.hurtTime > 0) {
            mc.player.setVelocity(0, 0.42f, 0);
            mc.player.jump();
            shouldReset = false;
        }
    }

    @Override
    public void onDisable() { shouldReset = false; }
}
