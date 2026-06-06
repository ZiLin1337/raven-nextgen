package keystrokesmod.lag.queue.node.impl;

import net.minecraft.network.packet.Packet;
import keystrokesmod.lag.api.EnumLagDirection;
import keystrokesmod.lag.handler.AbstractFastTrackProvider;
import keystrokesmod.lag.queue.node.api.AbstractLagNode;

import org.jetbrains.annotations.NotNull;

public final class PacketLagNode extends AbstractLagNode {

    private final Object packet;
    private final @NotNull EnumLagDirection direction;
    private final long queuedAtMs;

    public PacketLagNode(final Object packet, final @NotNull EnumLagDirection direction) {
        this.packet = packet;
        this.direction = direction;
        this.queuedAtMs = System.currentTimeMillis();
    }

    public long getQueuedAtMs() {
        return queuedAtMs;
    }

    public void goThrough(final @NotNull AbstractFastTrackProvider fastTrack) {
        if (direction == EnumLagDirection.OUTBOUND) {
            fastTrack.forPacket(packet);
        }
        direction.passThroughChannel(packet);
    }

}
