package keystrokesmod.event;

/**
 * Base event class for 1.21.4
 */
public abstract class Event {
    private boolean cancelled = false;
    
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public void cancel() { this.cancelled = true; }
}