package keystrokesmod.event.impl;

/**
 * 1.21.4 compatible KeyPress event
 */
public class KeyPressEvent {
    public final int key;
    public final int action;
    public final int modifiers;
    
    public KeyPressEvent(int key, int action, int modifiers) {
        this.key = key;
        this.action = action;
        this.modifiers = modifiers;
    }
}
