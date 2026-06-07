package keystrokesmod.module.impl.fun;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3dd;

public class SlyPort extends Module {
    public SliderSetting range;
    public ButtonSetting playSound;
    public ButtonSetting playersOnly;
    public ButtonSetting aim;

    public SlyPort() {
        super("SlyPort", category.fun);
        this.registerSetting(new DescriptionSetting("Teleport behind enemies."));
        this.registerSetting(range = new SliderSetting("Range", 6.0D, 2.0D, 15.0D, 1.0D));
        this.registerSetting(aim = new ButtonSetting("Aim", true));
        this.registerSetting(playSound = new ButtonSetting("Play sound", true));
        this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
    }

    @Override
    public void onEnable() {
        Entity en = this.getNearestTarget();
        if (en != null) {
            this.teleport(en);
        }

        this.disable();
    }

    private void teleport(Entity en) {
        if (playSound.isToggled() {
            mc.player.playSound("mob.endermen.portal", 1.0F, 1.0F);
        }

        Vec3d vec = en.getLookVec();
        double x = en.posX - vec.xCoord * 2.5D;
        double z = en.posZ - vec.zCoord * 2.5D;
        mc.player.setPosition(x, mc.player.getY(), z);
        if (aim.isToggled() {
            Utils.aim(en, 0.0F, false);
        }

    }

    private Entity getNearestTarget() {
        Entity en = null;
        double range = Math.pow(this.range.getInput(), 2.0D);
        double dist = range + 1.0D;

        for (Entity entities : mc.world.getEntities() {
            if (entities == mc.player) {
                continue;
            }
            if (!(entities instanceof LivingEntity) {
                continue;
            }
            if (((LivingEntity) entities).deathTime != 0) {
                continue;
            }
            if (this.playersOnly.isToggled() && !(entities instanceof PlayerEntity) {
                continue;
            }
            if (AntiBot.isBot(entities) {
                continue;
            }
            double distance = mc.player.getDistanceSqToEntity(entities);
            if (distance <= range && dist >= distance) {
                dist = distance;
                en = entities;
            }
        }

        return en;
    }
}