package keystrokesmod.event;

import net.minecraft.network.packet.Packet;

public class PreExplosionPacketEvent extends Event {
    private Packet<?> packet;
    public PreExplosionPacketEvent(Packet<?> packet) { this.packet = packet; }
    public Packet<?> getPacket() { return packet; }
}
