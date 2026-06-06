package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

import net.minecraft.item.ItemStack;

public class SafeWalk extends Module {
    private SliderSetting sneakDelay;
    private SliderSetting motion;
    public ButtonSetting sneak;
    public static ButtonSetting blocksOnly, pitchCheck, disableOnForward;

    private int unsneakDelayTicks = 0;
    public boolean isSneaking;

    public SafeWalk() {
        super("SafeWalk", Module.category.player, 0);
        this.registerSetting(sneakDelay = new SliderSetting("Sneak delay", " tick", 0, 0, 20, 1));
        this.registerSetting(motion = new SliderSetting("Motion", "x", 1.0, 0.5, 1.2, 0.01));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(disableOnForward = new ButtonSetting("Disable on forward", false));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
        this.registerSetting(sneak = new ButtonSetting("Sneak", false));
    }

    @Override
    public void onDisable() {
        if (sneak.isToggled() && Utils.isEdgeOfBlock()) {
            this.setSneakState(false);
        }
        isSneaking = false;
        unsneakDelayTicks = 0;
    }

    @Override
    public void onUpdate() {
        if (motion.getInput() != 1.0 && mc.player.onGround && Utils.isMoving() && settingsMet()) {
            mc.player.motionX *= motion.getInput();
            mc.player.motionZ *= motion.getInput();
        }
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (!sneak.isToggled() || !Utils.nullCheck()) {
            return;
        }
        boolean edge = mc.player.onGround && Utils.isEdgeOfBlock();
        if (edge) {
            if (!settingsMet()) {
                this.setSneakState(false);
                return;
            }
            if (!this.isSneaking) {
                this.setSneakState(true);
                unsneakDelayTicks = (int) sneakDelay.getInput();
            }
        }
        else {
            if (this.isSneaking) {
                if (!settingsMet()) {
                    this.setSneakState(false);
                    return;
                }
                if (unsneakDelayTicks > 0) {
                    unsneakDelayTicks--;
                    return;
                }
                this.setSneakState(false);
            }
        }
        if (this.isSneaking && (mc.player.capabilities.isFlying || !settingsMet())) {
            this.setSneakState(false);
        }
    }

    
    public void onGuiOpen(GuiOpenEvent e) {
        if (sneak.isToggled() && e.gui == null) {
            this.isSneaking = mc.player.isSneaking();
        }
    }

    private void setSneakState(boolean sneakState) {
        if (!sneakState) {
            unsneakDelayTicks = 0;
        }
        if (this.isSneaking == sneakState) {
            return;
        }

        if (!sneakState && Utils.isBindDown(mc.gameSettings.keyBindSneak)) {
            return;
        }

        InputUtil.setKeyPressed(mc.gameSettings.keyBindSneak.getKeyCode(), sneakState);
        if (sneakState) {
            KeyBinding.onTick(mc.gameSettings.keyBindSneak.getKeyCode());
        }
        this.isSneaking = sneakState;
    }

    public static boolean canSafeWalk() {
        if (ModuleManager.safeWalk != null && ModuleManager.safeWalk.isEnabled()) {
            if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                return false;
            }
            if (pitchCheck.isToggled() && mc.player.rotationPitch < 70) {
                return false;
            }
            if (blocksOnly.isToggled()) {
                ItemStack held = mc.player.getHeldItem();
                if (held == null || !(held.getItem() instanceof ItemBlock)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean settingsMet() {
        if (blocksOnly.isToggled()) {
            ItemStack held = mc.player.getHeldItem();
            if (held == null || !(held.getItem() instanceof ItemBlock)) {
                return false;
            }
        }
        if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            return false;
        }
        if (pitchCheck.isToggled() && mc.player.rotationPitch < 70.0f) {
            return false;
        }
        return true;
    }
}