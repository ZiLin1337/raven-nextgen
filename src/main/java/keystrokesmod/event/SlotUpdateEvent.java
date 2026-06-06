package keystrokesmod.event;

public class SlotUpdateEvent extends Event {
    private int slot, prevSlot;
    public SlotUpdateEvent(int slot) { this.slot = slot; }
    public SlotUpdateEvent(int slot, int prevSlot) { this.slot = slot; this.prevSlot = prevSlot; }
    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }
    public int getPrevSlot() { return prevSlot; }
}
