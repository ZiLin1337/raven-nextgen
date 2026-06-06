package keystrokesmod.event;

public class StepHeightEvent extends Event {
    private float stepHeight;
    public StepHeightEvent(float stepHeight) { this.stepHeight = stepHeight; }
    public float getStepHeight() { return stepHeight; }
    public void setStepHeight(float stepHeight) { this.stepHeight = stepHeight; }
}
