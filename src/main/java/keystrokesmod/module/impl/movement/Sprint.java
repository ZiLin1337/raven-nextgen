package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.effect.StatusEffects;

public class Sprint extends Module {
    public SliderSetting mode;
    private ButtonSetting blindJump;
    private static final String[] MODES = new String[]{"Vanilla", "Omni"};

    public Sprint() {
        super("Sprint", category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", 0, MODES));
        this.registerSetting(blindJump = new ButtonSetting("Blind jump", false));
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        if (mc.player.hasStatusEffect(StatusEffects.BLINDNESS) && !blindJump.isToggled() {
            mc.player.setSprinting(false);
            return;
        }
        if (mode.getInput() == 1) {
            mc.player.setSprinting(true);
            return;
        }
        if (mc.player.input.movementForward > 0) {
            mc.player.setSprinting(true);
        } else {
            mc.player.setSprinting(false);
        }
    }
}