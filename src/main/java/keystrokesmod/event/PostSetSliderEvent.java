package keystrokesmod.event;

public class PostSetSliderEvent extends Event {
    private double prevValue, newValue;
    public PostSetSliderEvent(double prevValue, double newValue) { this.prevValue = prevValue; this.newValue = newValue; }
    public double getPrevValue() { return prevValue; }
    public double getNewValue() { return newValue; }
}
