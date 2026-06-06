package keystrokesmod.script.packet.serverbound;

import keystrokesmod.script.packet.PacketMappings;
import net.minecraft.network.packet.Packet;

public class CPacket {
    public String name;
    public Packet<?> packet;

    public CPacket(Packet<?> packet) {
        if (packet == null) return;
        this.packet = packet;
        this.name = PacketMappings.getPacketName(packet);
    }

    public Packet<?> convert() {
        return packet;
    }
}
