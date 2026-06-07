package keystrokesmod.event.impl;

import net.minecraft.network.packet.Packet;

/**
 * 1.21.4 compatible SendPacket event
 */
public class SendPacketEvent {
    public final Packet<?> packet;
    public boolean cancelled = false;
    
    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}
