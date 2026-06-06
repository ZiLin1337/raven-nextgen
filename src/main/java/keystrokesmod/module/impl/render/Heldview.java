package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Heldview extends Module {
    public static Heldview instance;
    private SliderSetting speed, fov;

    public Heldview() {
        super("Heldview", category.render);
        instance = this;
        this.registerSetting(speed = new SliderSetting("Speed", 1.0, 0.1, 2.0, 0.1));
        this.registerSetting(fov = new SliderSetting("FOV", 70, 30, 120, 1));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }
}
