package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class NoHurtCam extends Module {
    public static NoHurtCam instance;
    public final SliderSetting multiplier = new SliderSetting("Multiplier", 0, 0, 100, 1);

    public NoHurtCam() {
        super("NoHurtCam", category.render);
        instance = this;
        registerSetting(multiplier);
    }

    public static float getMultiplier() {
        return instance == null ? 1.0F : (float) instance.multiplier.getInput() / 100.0F;
    }
}