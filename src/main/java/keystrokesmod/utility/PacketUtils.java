package keystrokesmod.utility;
import net.minecraft.network.packet.Packet;

import keystrokesmod.Raven;

import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.util.math.Direction;

public class PacketUtils implements IMinecraftInstance {
    private static final Set<Packet<?>> skipSendEvent = Collections.newSetFromMap(
            Collections.synchronizedMap(new IdentityHashMap<Packet<?>, Boolean>())
    );
    private static final Set<Packet<?>> skipReceiveEvent = Collections.newSetFromMap(
            Collections.synchronizedMap(new IdentityHashMap<Packet<?>, Boolean>())
    );

    public static boolean consumeSendEventSkip(Packet<?> packet) {
        return skipSendEvent.remove(packet);
    }

    public static boolean consumeReceiveEventSkip(Packet<?> packet) {
        return skipReceiveEvent.remove(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        if (packet == null || packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        skipSendEvent.add(packet);
        Raven.mc.player.sendQueue.networkHandler.sendPacket(packet);
    }

    public static void receivePacketNoEvent(Packet packet) {
        try {
            packet.processPacket(Raven.mc.getNetHandler());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendReleasePacket() {
        mc.player.sendQueue.networkHandler.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, DOWN));
    }
}
