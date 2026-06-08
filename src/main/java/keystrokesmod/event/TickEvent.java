package keystrokesmod.event;

public class TickEvent extends Event {
    public enum Phase {
        START, END
    }

    public static class RenderTickEvent extends TickEvent {
        public final Phase phase;
        public final float tickDelta;

        public RenderTickEvent(Phase phase, float tickDelta) {
            this.phase = phase;
            this.tickDelta = tickDelta;
        }
    }
}
