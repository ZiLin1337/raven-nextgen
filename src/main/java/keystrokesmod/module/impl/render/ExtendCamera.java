package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ExtendCamera extends Module {
    private SliderSetting distance;

    public ExtendCamera() {
        super("ExtendCamera", category.render);
        this.registerSetting(distance = new SliderSetting("Distance", 10, 2, 50, 1));
    }

    public static double getDistance() {
        ExtendCamera ec = (ExtendCamera) keystrokesmod.module.ModuleManager.moduleManager.getModule("ExtendCamera");
        return (ec != null && ec.isEnabled()) ? ec.distance.getInput() : 4.0;
    }
}
