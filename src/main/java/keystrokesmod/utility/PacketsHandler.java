package keystrokesmod.utility;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.concurrent.atomic.AtomicInteger;

public class PacketsHandler implements IMinecraftInstance {
    public AtomicInteger playerSlot = new AtomicInteger(-1);
    public AtomicInteger serverSlot = new AtomicInteger(-1);
    private final boolean handleSlots = true;public void onSendPacket(SendPacketEvent e) {
        if (e.isCanceled()) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (packet instanceof C09PacketHeldItemChange && handleSlots) {
            C09PacketHeldItemChange slotPacket = (C09PacketHeldItemChange) packet;
            int slotId = slotPacket.getSlotId();
            playerSlot.set(slotId);
            serverSlot.set(slotId);
        }
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (e.getPacket() instanceof S09PacketHeldItemChange && handleSlots) {
            S09PacketHeldItemChange packet = (S09PacketHeldItemChange) e.getPacket();
            int index = packet.getHeldItemHotbarIndex();
            if (index >= 0 && index < InventoryPlayer.getHotbarSize()) {
                serverSlot.set(index);
            }
        }
        else if (e.getPacket() instanceof S0CPacketSpawnPlayer && MinecraftClient.getInstance().thePlayer != null && handleSlots) {
            S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) e.getPacket();
            if (packet.getEntityID() != MinecraftClient.getInstance().thePlayer.getEntityId()) {
                return;
            }
            playerSlot.set(-1);
        }
    }
}