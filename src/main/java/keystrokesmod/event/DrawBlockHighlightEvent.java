package keystrokesmod.event;

public class DrawBlockHighlightEvent extends Event {
    private boolean cancelled;
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCanceled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}