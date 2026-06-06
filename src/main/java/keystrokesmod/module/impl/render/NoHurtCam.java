package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class NoHurtCam extends Module {
    public static NoHurtCam instance;
    private SliderSetting multiplier;

    public NoHurtCam() {
        super("NoHurtCam", category.render);
        instance = this;
        this.registerSetting(multiplier = new SliderSetting("Multiplier", 0, 0, 100, 1));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }

    public static float getMultiplier() {
        return (instance != null && instance.isEnabled()) ? (float) instance.multiplier.getInput() / 100f : 1.0f;
    }
}
