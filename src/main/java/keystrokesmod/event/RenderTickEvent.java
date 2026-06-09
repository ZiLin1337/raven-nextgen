package keystrokesmod.event;

public class RenderTickEvent extends TickEvent.RenderTickEvent {
    public RenderTickEvent(Phase phase, float tickDelta) {
        super(phase, tickDelta);
    }
}
