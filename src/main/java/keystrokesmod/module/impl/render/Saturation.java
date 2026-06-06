package keystrokesmod.module.impl.render;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

// Saturation: Shader APIs not available in 1.21.4 Fabric
// Stubbed out - saturation shader effect not supported
public class Saturation extends Module {
    private final SliderSetting saturation;

    public Saturation() {
        super("Saturation", category.render);
        this.registerSetting(saturation = new SliderSetting("Saturation", 1.5, 0.0, 5.0, 0.1));
    }
}
