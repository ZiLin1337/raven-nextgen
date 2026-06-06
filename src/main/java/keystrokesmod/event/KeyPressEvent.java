package keystrokesmod.event;

public class KeyPressEvent extends Event {
    private int keyCode;
    private boolean pressed;
    public KeyPressEvent(int keyCode, boolean pressed) { this.keyCode = keyCode; this.pressed = pressed; }
    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
    public boolean isPressed() { return pressed; }
}
