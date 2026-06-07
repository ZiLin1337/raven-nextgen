package keystrokesmod.lag.handler;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFastTrackProvider {

    public abstract void forPacket(final @NotNull Packet<?> packet);

}