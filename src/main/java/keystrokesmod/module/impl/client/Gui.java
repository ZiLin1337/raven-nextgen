package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenTextRenderer;

public class Gui extends Module {
    public static SliderSetting guiScale;
    public static SliderSetting scrollSpeed;
    public static ButtonSetting blurBackground;
    public static ButtonSetting rainBowOutlines;

    public Gui() {
        super("Gui", category.client);
        this.registerSetting(guiScale = new SliderSetting("Scale", 100, 50, 200, 5));
        this.registerSetting(scrollSpeed = new SliderSetting("Scroll speed", 120, 10, 240, 10));
        this.registerSetting(blurBackground = new ButtonSetting("Blur background", true));
        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", false));
    }

    public static double getGuiScale() {
        Gui gui = (Gui) keystrokesmod.module.ModuleManager.moduleManager.getModule("Gui");
        return (gui != null && gui.isEnabled()) ? guiScale.getInput() / 100.0 : 1.0;
    }
    public static RavenTextRenderer getClickGuiHeaderTextRenderer() { return FontManager.getClickGuiHeaderRenderer("Minecraft"); }
    public static RavenTextRenderer getClickGuiSettingTextRenderer() { return FontManager.getClickGuiSettingRenderer("Minecraft"); }
}
