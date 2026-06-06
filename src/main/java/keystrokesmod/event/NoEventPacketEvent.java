package keystrokesmod.event;

import net.minecraft.network.packet.Packet;

public class NoEventPacketEvent extends Event {
    private Packet<?> packet;
    public NoEventPacketEvent(Packet<?> packet) { this.packet = packet; }
    public Packet<?> getPacket() { return packet; }
}
