package keystrokesmod.module.impl.player;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {
    public ButtonSetting serverSide;
    private float prevYaw, prevPitch;

    public NoRotate() {
        super("NoRotate", category.player);
        this.registerSetting(serverSide = new ButtonSetting("Server side", false));
    }

    public void onPacketReceive(PlayerPositionLookS2CPacket packet) {
        prevYaw = packet.getYaw();
        prevPitch = packet.getPitch();
        if (serverSide.isToggled() {
            // Packet will be modified by mixin to restore original rotation
        }
    }

    public void onPreMotion(PreMotionEvent e) {
        if (!serverSide.isToggled() {
            e.setYaw(prevYaw);
            e.setPitch(prevPitch);
        }
    }
}
