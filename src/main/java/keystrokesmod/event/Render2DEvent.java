package keystrokesmod.event;

public class Render2DEvent extends Event {
    private final float partialTicks;
    private final int scaledWidth;
    private final int scaledHeight;
    
    public Render2DEvent(float partialTicks, int scaledWidth, int scaledHeight) {
        this.partialTicks = partialTicks;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
    }
    
    public float getPartialTicks() {
        return partialTicks;
    }
    
    public int getScaledWidth() {
        return scaledWidth;
    }
    
    public int getScaledHeight() {
        return scaledHeight;
    }
}