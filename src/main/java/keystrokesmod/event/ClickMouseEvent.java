package keystrokesmod.event;

public class ClickMouseEvent extends Event {
    private int button;
    private boolean state;
    public ClickMouseEvent() {}
    public ClickMouseEvent(int button, boolean state) { this.button = button; this.state = state; }
    public int getButton() { return button; }
    public boolean getState() { return state; }
}
