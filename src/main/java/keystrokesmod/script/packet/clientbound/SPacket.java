package keystrokesmod.script.packet.clientbound;

import keystrokesmod.script.packet.PacketMappings;
import net.minecraft.network.packet.Packet;

public class SPacket {
    public String name;
    public Packet<?> packet;

    public SPacket(Packet<?> packet) {
        this.packet = packet;
        this.name = PacketMappings.getPacketName(packet);
    }
}
