package keystrokesmod.lag.handler;

import net.minecraft.network.packet.Packet;
import keystrokesmod.event.ReceivePacketEvent;

import keystrokesmod.lag.api.EnumLagDirection;
import keystrokesmod.lag.api.LagRequest;
import keystrokesmod.lag.queue.BiTrackLagNodeQueue;
import net.minecraft.client.MinecraftClient;

import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public final class UnifiedLagHandler extends AbstractFastTrackProvider {

    private final @NotNull BiTrackLagNodeQueue queue = new BiTrackLagNodeQueue(this);

    private final @NotNull Set<Object> packetFastTrack = Collections.newSetFromMap(
            Collections.synchronizedMap(new IdentityHashMap<Packet<?>, Boolean>())
    );
    private volatile @Nullable Vec3d serverPosition;

    public void requestLag(final @NotNull LagRequest request) {
        queue.requestLag(request);
    }

    public void releaseExpiredPackets(final @NotNull EnumLagDirection direction, long maxAgeMs) {
        queue.releaseExpiredPackets(direction, maxAgeMs);
    }

    public @Nullable Vec3d getLastReleasedServerPosition() {
        return serverPosition;
    }public void onSendPacket(final @NotNull SendPacketEvent event) {
        if (MinecraftClient.getInstance().getNetHandler() == null) {
            queue.clear();
            clearServerPositions();
            return;
        }

        final @NotNull Packet<?> packet = event.getPacket();
        final boolean fastTracked = consumeFastTrack(packet);

        if (event.isCanceled()) {
            return;
        }

        if (fastTracked) {
            updateServerPosition(packet);
            return;
        }

        if (queue.tick(packet, EnumLagDirection.OUTBOUND)) {
            event.setCanceled(true);
            return;
        }

        updateServerPosition(packet);
    }

    
    public void onReceivePacket(final @NotNull ReceivePacketEvent event) {
        if (MinecraftClient.getInstance().getNetHandler() == null) {
            queue.clear();
            clearServerPositions();
            return;
        }

        final @NotNull Packet<?> packet = event.getPacket();
        final boolean fastTracked = consumeFastTrack(packet);

        if (event.isCanceled()) {
            return;
        }

        if (fastTracked) {
            return;
        }

        if (queue.tick(packet, EnumLagDirection.INBOUND)) {
            event.setCanceled(true);
        }
    }

    
    public void onGameTick(final @NotNull GameTickEvent event) {
        if (MinecraftClient.getInstance().getNetHandler() == null) {
            queue.clear();
            clearServerPositions();
            return;
        }

        queue.tick(null, null);
    }

    @Override
    public void forPacket(final Object packet) {
        packetFastTrack.add(packet);
    }

    private boolean consumeFastTrack(final Object packet) {
        return packetFastTrack.remove(packet);
    }

    private void updateServerPosition(final Object packet) {
        if (!(packet instanceof C03PacketPlayer)) {
            return;
        }

        C03PacketPlayer movementPacket = (C03PacketPlayer) packet;
        if (!movementPacket.isMoving()) {
            return;
        }

        serverPosition = new Vec3d(
                movementPacket.getPositionX(),
                movementPacket.getPositionY(),
                movementPacket.getPositionZ()
        );
    }

    private void clearServerPositions() {
        serverPosition = null;
    }

}
