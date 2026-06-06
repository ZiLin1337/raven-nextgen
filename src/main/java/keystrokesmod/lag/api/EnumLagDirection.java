package keystrokesmod.lag.api;

import keystrokesmod.utility.IMinecraftInstance;
import keystrokesmod.utility.Utils;

import net.minecraft.network.packet.Packet;
// TODO: Remove Forge net handler import
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"LambdaBodyCanBeCodeBlock", "unchecked"})
public enum EnumLagDirection implements IMinecraftInstance {
    INBOUND(
            packet -> {
                try {
                    ((Packet<INetHandlerPlayClient>) packet).processPacket(mc.getNetHandler());
                } catch (final @NotNull ThreadQuickExitException ignored) {
                    // minecraft uses an exception to indicate something getting scheduled... why?
                } catch (final @NotNull Exception e) {
                    Utils.sendDebugMessage("error while handling packet: " + packet.getClass().getSimpleName());
                }
            }
    ),
    OUTBOUND(
            packet -> {
                mc.getNetHandler().addToSendQueue(packet);
            }
    );

    public static final @NotNull Set<EnumLagDirection> ONLY_INBOUND = EnumSet.of(INBOUND);
    public static final @NotNull Set<EnumLagDirection> ONLY_OUTBOUND = EnumSet.of(OUTBOUND);
    public static final @NotNull Set<EnumLagDirection> BIDIRECTIONAL = EnumSet.allOf(EnumLagDirection.class);

    private final @NotNull Object channel;

    EnumLagDirection(
            final @NotNull Object channel
    ) {
        this.channel = channel;
    }

    public void passThroughChannel(final Object packet) {
        channel.accept(packet);
    }

}