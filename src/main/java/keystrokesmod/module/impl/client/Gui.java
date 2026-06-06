package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Gui extends Module {
    public static SliderSetting guiScale;
    public static ButtonSetting blurBackground;

    public Gui() {
        super("Gui", category.client);
        this.registerSetting(guiScale = new SliderSetting("Scale", 100, 50, 200, 5));
        this.registerSetting(blurBackground = new ButtonSetting("Blur background", true));
    }

    public static double getGuiScale() {
        Gui gui = (Gui) keystrokesmod.module.ModuleManager.moduleManager.getModule("Gui");
        return (gui != null && gui.isEnabled()) ? guiScale.getInput() / 100.0 : 1.0;
    }
}
