package keystrokesmod.utility;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

import java.util.concurrent.atomic.AtomicInteger;

public class PacketsHandler implements IMinecraftInstance {
    public AtomicInteger playerSlot = new AtomicInteger(-1);
    public AtomicInteger serverSlot = new AtomicInteger(-1);
    private final boolean handleSlots = true;

    
    public void onSendPacket(SendPacketEvent e) {
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
    }
}