package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class WorldTweaks extends Module {
    public static WorldTweaks instance;
    private SliderSetting skyColor, grassColor;
    private ButtonSetting removeRain, removeFog;

    public WorldTweaks() {
        super("WorldTweaks", category.render);
        instance = this;
        this.registerSetting(skyColor = new SliderSetting("Sky brightness", 100, 50, 200, 5));
        this.registerSetting(grassColor = new SliderSetting("Grass brightness", 100, 50, 200, 5));
        this.registerSetting(removeRain = new ButtonSetting("Remove rain", true));
        this.registerSetting(removeFog = new ButtonSetting("Remove fog", true));
    }

    public void onEnable() { instance = this; }
    public void onDisable() { instance = null; }
}
