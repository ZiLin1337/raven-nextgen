package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;

public class Velocity extends Module {
    public static SliderSetting horizontal;
    public static SliderSetting vertical;
    private SliderSetting chance;
    private ButtonSetting onlyWhileTargeting;
    private ButtonSetting disableS;
    public boolean disable;

    public Velocity() {
        super("Velocity", category.combat, 0);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", "%", 90.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(vertical = new SliderSetting("Vertical", "%", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(chance = new SliderSetting("Chance", "%", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(onlyWhileTargeting = new ButtonSetting("Only while targeting", false));
        this.registerSetting(disableS = new ButtonSetting("Disable while holding S", false));
        this.closetModule = true;
    }

    @Override
    public String getInfo() {
        return (int) horizontal.getInput() + "% " + (int) vertical.getInput() + "%";
    }

    @Override
    public void onEnable() {
        Raven.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        Raven.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    public void onTick(GameTickEvent e) {
        if (!Utils.nullCheck()) return;
        if (LongJump.stopVelocity || disable) return;
        if (ModuleManager.antiKnockback != null && ModuleManager.antiKnockback.isEnabled()) return;
        if (mc.player.maxHurtTime <= 0 || mc.player.hurtTime != mc.player.maxHurtTime) return;
        if (onlyWhileTargeting.isToggled() && mc.crosshairTarget == null) return;
        if (disableS.isToggled() && mc.options.backKey.isPressed()) return;

        double chanceVal = chance.getInput();
        if (chanceVal == 0) return;
        if (chanceVal != 100 && Math.random() >= chanceVal / 100.0D) return;

        double hFactor = horizontal.getInput() / 100.0D;
        double vFactor = vertical.getInput() / 100.0D;

        if (hFactor != 1.0D) {
            mc.player.setVelocity(
                mc.player.getVelocity().x * hFactor,
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * hFactor
            );
        }
        if (vFactor != 1.0D) {
            mc.player.setVelocity(
                mc.player.getVelocity().x,
                mc.player.getVelocity().y * vFactor,
                mc.player.getVelocity().z
            );
        }
    }
}