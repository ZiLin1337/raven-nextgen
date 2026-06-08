package keystrokesmod.event;

public class RenderWorldLastEvent extends Event {
    public final float tickDelta;

    public RenderWorldLastEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
