package keystrokesmod.module.impl.fun;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ExtraBobbing extends Module {
    private SliderSetting intensity;

    public ExtraBobbing() {
        super("ExtraBobbing", category.fun);
        this.registerSetting(intensity = new SliderSetting("Intensity", 2.0, 1.0, 5.0, 0.5));
    }

    public static float getIntensity() {
        ExtraBobbing eb = (ExtraBobbing) keystrokesmod.module.ModuleManager.moduleManager.getModule("ExtraBobbing");
        return (eb != null && eb.isEnabled()) ? (float) eb.intensity.getInput() : 1.0f;
    }
}
