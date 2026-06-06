package keystrokesmod.event;

public class MouseEvent extends Event {
    private final int button;
    private final boolean state;
    
    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }
    
    public int getButton() { return button; }
    public boolean getState() { return state; }
}
