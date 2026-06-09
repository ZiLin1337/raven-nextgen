package keystrokesmod.lag.queue.node.impl;

import keystrokesmod.lag.api.EnumLagDirection;
import keystrokesmod.lag.handler.AbstractFastTrackProvider;
import keystrokesmod.lag.queue.node.api.AbstractLagNode;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class PacketLagNode extends AbstractLagNode {
    public PacketLagNode(final @NotNull Packet<?> packet, final @NotNull EnumLagDirection direction) {
    }

    public long getQueuedAtMs() {
        return 0L;
    }

    public void goThrough(final @NotNull AbstractFastTrackProvider fastTrack) {
    }
}