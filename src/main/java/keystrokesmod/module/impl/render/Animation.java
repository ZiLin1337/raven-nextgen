package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class Animation extends Module {
    public static Animation instance;
    public SliderSetting mode, speed;
    public ButtonSetting smoothSwing;

    public Animation() {
        super("Animation", category.render);
        instance = this;
        this.registerSetting(mode = new SliderSetting("Mode", 0, new String[]{"1.7", "1.8", "Slide", "Push"}));
        this.registerSetting(speed = new SliderSetting("Speed", 1.0, 0.1, 2.0, 0.1));
        this.registerSetting(smoothSwing = new ButtonSetting("Smooth swing", true));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }
}
