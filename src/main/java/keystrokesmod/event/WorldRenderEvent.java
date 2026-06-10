package keystrokesmod.event;

public class WorldRenderEvent {
    private final float tickDelta;
    
    public WorldRenderEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
    
    public float getTickDelta() {
        return tickDelta;
    }
}
