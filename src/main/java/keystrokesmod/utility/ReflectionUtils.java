package keystrokesmod.utility;

import net.minecraft.client.option.KeyBinding;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtils {
    public static Field button;
    public static Field buttonstate;
    public static Field buttons;
    public static HashMap<Class, Field> containerInventoryPlayer = new HashMap<>();
    public static boolean ERROR = false;
    public static Map<String, KeyBinding> keybinds = new HashMap<>();

    public static void setupFields() {
    }

    public static void setKeyBindings() {
    }

    public static void setButton(int button, boolean state) {
    }

    public static boolean setItemInUse(boolean blocking) {
        return blocking;
    }
}
