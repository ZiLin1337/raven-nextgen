package keystrokesmod.lag.handler;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractFastTrackProvider {

    public abstract void forPacket(final @NotNull Packet<?> packet);

}