package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Timer extends Module {
    public final SliderSetting speed;

    public Timer() {
        super("Timer", category.movement);
        this.registerSetting(speed = new SliderSetting("Speed", 1.0, 0.1, 5.0, 0.1));
    }
}
