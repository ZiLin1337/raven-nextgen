package keystrokesmod.module.impl.combat;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

// RawInput: JInput/MouseHelper not available in 1.21.4 Fabric
// Stubbed out - raw mouse input not supported
public class RawInput extends Module {
    private final SliderSetting sensitivity;

    public RawInput() {
        super("RawInput", category.combat, 0);
        this.registerSetting(sensitivity = new SliderSetting("Sensitivity", 1.0, 0.1, 5.0, 0.1));
    }
}
