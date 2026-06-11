package keystrokesmod.mixin.impl.network;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HandshakeC2SPacket.class)
public class MixinHandshakeC2SPacket {
    // AntiStaff - 握手包
}
