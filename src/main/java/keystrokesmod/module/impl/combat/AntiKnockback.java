package keystrokesmod.module.impl.combat;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

import org.lwjgl.glfw.GLFW;

public class AntiKnockback extends Module {
    private SliderSetting horizontal;
    private SliderSetting vertical;
    private ButtonSetting disableInLobby;
    private ButtonSetting cancelBurning;
    private ButtonSetting cancelExplosion;
    private ButtonSetting cancelWhileFalling;
    private ButtonSetting cancelOffGround;
    private SliderSetting boostMultiplier;
    private ButtonSetting boostWithLMB;

    public boolean disable;

    public AntiKnockback() {
        super("AntiKnockback", category.combat);
        this.registerSetting(new DescriptionSetting("Overrides Velocity."));
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(vertical = new SliderSetting("Vertical", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(disableInLobby = new ButtonSetting("Disable in lobby", false));
        this.registerSetting(cancelBurning = new ButtonSetting("Cancel burning", true));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion", true));
        this.registerSetting(cancelWhileFalling = new ButtonSetting("Cancel while falling", true));
        this.registerSetting(cancelOffGround = new ButtonSetting("Cancel off ground", true));
        this.registerSetting(boostMultiplier = new SliderSetting("Damage boost", "x", 1, 0.5, 2.5, 0.01));
        this.registerSetting(boostWithLMB = new ButtonSetting("Boost with LMB", false));
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck() || LongJump.stopVelocity || e.isCanceled()) {
            return;
        }
        if ((e.getPacket() instanceof EntityVelocityUpdateS2CPacket || e.getPacket() instanceof ExplosionS2CPacket) && !disable) {
            if (disableInLobby.isToggled() && Utils.isLobby()) {
                return;
            }
            if (!cancelConditions()) {
                e.setCanceled(cancel() || cancelExplosion.isToggled());
            }
        }
    }
    private boolean cancel() {
        return (vertical.getInput() == 0 && horizontal.getInput() == 0);
    }

    @Override
    public String getInfo() {
        return (int) horizontal.getInput() + "%" + " " + (int) vertical.getInput() + "%";
    }

    private boolean cancelConditions() {
        if (mc.player != null) {
            if (cancelWhileFalling.isToggled() && mc.player.fallDistance > 0) {
                return true;
            }
            if (cancelOffGround.isToggled() && !mc.player.isOnGround()) {
                return true;
            }
        }
        return false;
    }
}