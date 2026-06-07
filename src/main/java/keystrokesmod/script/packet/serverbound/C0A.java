package keystrokesmod.script.packet.serverbound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

/**
 * C0A: Animation packet (hand swing)
 * 1.21.4 equivalent: HandSwingC2SPacket
 */
public class C0A extends CPacket {
    public C0A() {
        super(new HandSwingC2SPacket(
            MinecraftClient.getInstance().player != null 
                ? MinecraftClient.getInstance().player.getActiveHand() 
                : Hand.MAIN_HAND
        ));
    }
}
