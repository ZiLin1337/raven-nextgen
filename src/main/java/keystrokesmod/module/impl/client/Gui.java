package keystrokesmod.module.impl.client;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class Gui extends Module {
    public static SliderSetting scrollSpeed;
    public static ButtonSetting rainBowOutlines;

    public Gui() {
        super("Gui", category.client);
        scrollSpeed = new SliderSetting("Scroll speed", 3, 1, 10, 1);
        rainBowOutlines = new ButtonSetting("Rainbow outlines", false);
        registerSetting(scrollSpeed);
        registerSetting(rainBowOutlines);
    }

    public static double getGuiScale() {
        return 1.0;
    }

    public static Object getClickGuiHeaderTextRenderer() {
        return null;
    }

    public static Object getClickGuiSettingTextRenderer() {
        return null;
    }
}
