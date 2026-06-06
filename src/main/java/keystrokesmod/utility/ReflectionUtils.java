package keystrokesmod.utility;

// import IMixinItemRenderer removed
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.inventory.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class ReflectionUtils {
    public static Field button;
    public static Field buttonstate;
    public static Field buttons;

    public static HashMap<Class, Field> containerPlayerInventory = new HashMap<>();

    private static List<Class> containerClasses = Arrays.asList(GuiFurnace.class, GuiBrewingStand.class, GuiEnchantment.class, ContainerHopper.class, GuiDispenser.class, ContainerWorkbench.class, ContainerMerchant.class, ContainerHorseInventory.class);

    public static boolean ERROR = false; // Set to true to alert the player when reflection fails

    public static Map<String, KeyBinding> keybinds = new HashMap<>();

    public static void setupFields() {
        try {
            button = MouseEvent.class.getDeclaredField("button");
            buttonstate = MouseEvent.class.getDeclaredField("buttonstate");
            buttons = Mouse.class.getDeclaredField("buttons");
            for (Class clazz : containerClasses) {
                for (Field field : clazz.getDeclaredFields()) {
                    addToMap(clazz, field);
                }
            }
        }
        catch (Exception var2) {
            System.out.println("There was an error, relaunch the game.");
            var2.printStackTrace();
            ERROR = true;
        }
    }

    public static void setKeyBindings() {
        for (KeyBinding keyBind : MinecraftClient.getInstance().gameSettings.keyBindings) {
            String keyName = keyBind.getKeyDescription().replaceFirst("key\\.", "");
            keybinds.put(keyName, keyBind);
        }
    }

    public static void setButton(int button, boolean state) {
        if (ReflectionUtils.button != null && buttonstate != null && buttons != null) {
            MouseEvent m = new MouseEvent();
            try {
                ReflectionUtils.button.setAccessible(true);
                ReflectionUtils.button.set(m, button);
                buttonstate.setAccessible(true);
                buttonstate.set(m, state);
                MinecraftForge.EVENT_BUS.post(m);
                buttons.setAccessible(true);
                ByteBuffer bf = (ByteBuffer) buttons.get(null);
                buttons.setAccessible(false);
                bf.put(button, (byte) (state ? 1 : 0));
            }
            catch (IllegalAccessException var4) {
            }
        }
    }

    private static void addToMap(Class clazz, Field field) {
        if (field == null || field.getType() != IInventory.class) {
            return;
        }
        field = ReflectionHelper.findField(clazz, field.getName());
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        containerPlayerInventory.put(clazz, field);
    }

    public static boolean setItemInUse(boolean blocking) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getItemRenderer() != null) {
            ((IMixinItemRenderer) mc.getItemRenderer()).setRenderItemInUse(blocking);
        }
        return blocking;
    }

}
