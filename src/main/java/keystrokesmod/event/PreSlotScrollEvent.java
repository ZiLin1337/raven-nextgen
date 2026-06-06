package keystrokesmod.event;
public class PreSlotScrollEvent extends Event {
    private int targetSlot;
    private int currentSlot;
    private boolean cancelled;
    public PreSlotScrollEvent(int targetSlot, int currentSlot) {
        this.targetSlot = targetSlot;
        this.currentSlot = currentSlot;
    }
    public int getTargetSlot() { return targetSlot; }
    public int getCurrentSlot() { return currentSlot; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}