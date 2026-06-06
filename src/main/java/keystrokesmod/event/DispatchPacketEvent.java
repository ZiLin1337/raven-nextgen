package keystrokesmod.event;

import net.minecraft.network.packet.Packet;

public class DispatchPacketEvent extends Event {
    private Packet<?> packet;
    public DispatchPacketEvent(Packet<?> packet) { this.packet = packet; }
    public Packet<?> getPacket() { return packet; }
}
