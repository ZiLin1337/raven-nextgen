package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class NoFog extends Module {
    public static boolean enabledInstance;
    private ButtonSetting nether;

    public NoFog() {
        super("NoFog", category.render);
        this.registerSetting(nether = new ButtonSetting("In nether too", true));
    }

    public void onEnable() { enabledInstance = true; }
    public void onDisable() { enabledInstance = false; }

    public static boolean isEnabled() { return enabledInstance; }
}
