package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Fly extends Module {
    public final SliderSetting mode;
    public final ButtonSetting keepY;
    public final SliderSetting horizontalSpeed;
    public final SliderSetting verticalSpeed;

    public Fly() {
        super("Fly", category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", 0, new String[]{"Vanilla"}));
        this.registerSetting(keepY = new ButtonSetting("Keep Y", false));
        this.registerSetting(horizontalSpeed = new SliderSetting("Horizontal Speed", 1.0, 0.1, 5.0, 0.1));
        this.registerSetting(verticalSpeed = new SliderSetting("Vertical Speed", 1.0, 0.1, 5.0, 0.1));
    }
}
