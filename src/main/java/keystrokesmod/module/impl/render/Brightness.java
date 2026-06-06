package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import meteordevelopment.orbit.EventHandler;

public class Brightness extends Module {
    private SliderSetting gamma;

    public Brightness() {
        super("Brightness", category.render);
        this.registerSetting(gamma = new SliderSetting("Gamma", 100, 10, 200, 5));
    }

    public void onEnable() {
        if (mc.options != null) mc.options.getGamma().setValue(100.0);
    }

    public void onDisable() {
        if (mc.options != null) mc.options.getGamma().setValue(1.0);
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.options != null) mc.options.getGamma().setValue(gamma.getInput());
    }
}
