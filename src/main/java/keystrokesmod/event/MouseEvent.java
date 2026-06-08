package keystrokesmod.event;

public class MouseEvent extends Event {
    public final int button;
    public final int action;
    public final double mouseX;
    public final double mouseY;

    public MouseEvent(int button, int action, double mouseX, double mouseY) {
        this.button = button;
        this.action = action;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
