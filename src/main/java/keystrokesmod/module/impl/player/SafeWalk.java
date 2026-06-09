package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class SafeWalk extends Module {
    public final ButtonSetting sneak = new ButtonSetting("Sneak", false);
    public boolean isSneaking = false;

    public SafeWalk() {
        super("SafeWalk", category.player);
        registerSetting(sneak);
    }

    public static boolean canSafeWalk() {
        return false;
    }
}