package keystrokesmod.script.packet.serverbound;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

/**
 * C0D: Close window packet
 * 1.21.4 equivalent: CloseHandledScreenC2SPacket
 */
public class C0D extends CPacket {
    public int syncId;

    public C0D(int syncId) {
        super(new CloseHandledScreenC2SPacket(syncId));
        this.syncId = syncId;
    }
}
