package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ClickGUI extends Module {
    public static SliderSetting colorR, colorG, colorB;

    public ClickGUI() {
        super("ClickGUI", category.render);
        this.registerSetting(colorR = new SliderSetting("Red", 150, 0, 255, 1));
        this.registerSetting(colorG = new SliderSetting("Green", 0, 0, 255, 1));
        this.registerSetting(colorB = new SliderSetting("Blue", 200, 0, 255, 1));
    }

    public void onEnable() {
        if (mc.currentScreen != Raven.clickGui) mc.setScreen(Raven.clickGui);
        disable();
    }
}
