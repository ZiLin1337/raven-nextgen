package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.setting.impl.ColorSetting;

public class ColorComponent extends Component {
    public final ColorSetting colorSetting;
    public boolean expanded = false;
    public float xOffset = 0;

    public ColorComponent(ColorSetting colorSetting, ModuleComponent p, float o) {
        this.colorSetting = colorSetting;
    }

    public float getExpandedHeight() {
        return 12.0F;
    }

    public float getAnimationProgress() {
        return 0.0F;
    }

    public void restoreExpandedState(boolean wasExpanded) {
        this.expanded = wasExpanded;
    }
}