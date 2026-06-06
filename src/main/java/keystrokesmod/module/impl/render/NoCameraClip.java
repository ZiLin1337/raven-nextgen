package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class NoCameraClip extends Module {
    public static NoCameraClip instance;
    private SliderSetting distance;

    public NoCameraClip() {
        super("NoCameraClip", category.render);
        instance = this;
        this.registerSetting(distance = new SliderSetting("Distance", 10, 2, 50, 1));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }

    public static double getDistance() {
        return (instance != null && instance.isEnabled()) ? instance.distance.getInput() : 4.0;
    }
}
