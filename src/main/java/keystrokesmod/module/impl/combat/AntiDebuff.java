package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class AntiDebuff extends Module {
    public ButtonSetting removeNausea = new ButtonSetting("Remove nausea", true);
    public ButtonSetting removeBlindness = new ButtonSetting("Remove blindness", true);
    public ButtonSetting removeSideEffects = new ButtonSetting("Remove side effects", true);

    public AntiDebuff() {
        super("AntiDebuff", Module.category.combat);
        registerSetting(removeNausea);
        registerSetting(removeBlindness);
        registerSetting(removeSideEffects);
    }

    public boolean canRemoveNausea(Object effect) {
        return isEnabled() && removeNausea.isToggled();
    }

    public boolean canRemoveBlindness(Object effect) {
        return isEnabled() && removeBlindness.isToggled();
    }
}