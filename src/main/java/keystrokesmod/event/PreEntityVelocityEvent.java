package keystrokesmod.event;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class PreEntityVelocityEvent extends Event {
    private boolean cancelled;

    public PreEntityVelocityEvent(EntityVelocityUpdateS2CPacket packet) {
    }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean c) { this.cancelled = c; }
}
