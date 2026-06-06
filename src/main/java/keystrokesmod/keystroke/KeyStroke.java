package keystrokesmod.keystroke;

import keystrokesmod.Raven;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

public class KeyStroke {
    private static KeyStroke instance;
    private boolean[] keyStates = new boolean[256];
    private int[] keyPressTimes = new int[256];
    private int tick;

    public KeyStroke() {
        instance = this;
    }

    public static KeyStroke get() {
        if (instance == null) instance = new KeyStroke();
        return instance;
    }

    public void onTick() {
        tick++;
        long handle = Raven.mc.getWindow().getHandle();
        for (int i = 0; i < 256; i++) {
            boolean pressed = GLFW.glfwGetKey(handle, i) == GLFW.GLFW_PRESS;
            if (pressed && !keyStates[i]) keyPressTimes[i] = tick;
            keyStates[i] = pressed;
        }
    }

    public boolean isKeyDown(int key) {
        if (key < 0 || key >= 256) return false;
        return keyStates[key];
    }

    public boolean wasKeyPressed(int key) {
        if (key < 0 || key >= 256) return false;
        return keyStates[key] && keyPressTimes[key] == tick;
    }

    public int getKeyPressTime(int key) {
        if (key < 0 || key >= 256) return 0;
        return keyPressTimes[key];
    }

    public void resetKeyState(int key) {
        if (key >= 0 && key < 256) keyStates[key] = false;
    }
}
