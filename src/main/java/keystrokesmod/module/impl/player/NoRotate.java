package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", Module.category.render);
    }
    
    public void onPlayerPositionLookPre() {
    }
    
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
    }
}
