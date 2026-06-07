package keystrokesmod.module.impl.player;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class DelayRemover extends Module {
    private ButtonSetting attackDelay, useDelay;

    public DelayRemover() {
        super("DelayRemover", category.player);
        this.registerSetting(attackDelay = new ButtonSetting("Remove attack delay", true));
        this.registerSetting(useDelay = new ButtonSetting("Remove use delay", true));
    }

    public void onUpdate() {
        if (mc.player != null && mc.interactionManager != null)) {
            if (attackDelay.isToggled()) mc.player.resetLastAttackedTicks();
            if (useDelay.isToggled()) mc.itemUseCooldown = 0;
        }
    }
}
