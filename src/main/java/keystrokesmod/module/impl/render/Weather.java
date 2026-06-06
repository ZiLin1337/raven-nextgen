package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Weather extends Module {
    public SliderSetting lightning = new SliderSetting("Lightning", 0, 0, 1, 0.1);
    public ButtonSetting rain = new ButtonSetting("Rain", true);
    public SliderSetting time = new SliderSetting("Time", 0, 0, 24000, 1000);

    public Weather() {
        super("Weather", Module.category.render);
        registerSetting(lightning);
        registerSetting(rain);
        registerSetting(time);
    }
}