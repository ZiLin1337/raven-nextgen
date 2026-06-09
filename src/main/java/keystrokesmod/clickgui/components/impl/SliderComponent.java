package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.setting.impl.SliderSetting;

public class SliderComponent extends Component {
    public final SliderSetting sliderSetting;
    public boolean heldDown;
    public float xOffset;
    public SliderComponent(SliderSetting setting, ModuleComponent p, float o) {
        this.sliderSetting = setting;
    }
    public void onSliderChange() {}
}
