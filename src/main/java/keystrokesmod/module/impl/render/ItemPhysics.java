package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ItemPhysics extends Module {
    public static ItemPhysics instance;
    private ButtonSetting enableSpin, enableBounce;
    private SliderSetting spinSpeed;

    public ItemPhysics() {
        super("ItemPhysics", category.render);
        instance = this;
        this.registerSetting(enableSpin = new ButtonSetting("Enable spin", true));
        this.registerSetting(enableBounce = new ButtonSetting("Enable bounce", true));
        this.registerSetting(spinSpeed = new SliderSetting("Spin speed", 1.0, 0.1, 3.0, 0.1));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }
}
