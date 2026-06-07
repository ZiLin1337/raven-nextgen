package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.mixin.impl.accessor.IAccessorMinecraft;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemFishingRod;
// Removed Forge event

public class RodAimbot extends Module {
    private SliderSetting fov;
    private SliderSetting predicatedTicks;
    private SliderSetting distance;
    private ButtonSetting aimInvis;
    private ButtonSetting ignoreTeammates;
    public boolean rotate;
    private boolean rightClick;
    private PlayerEntity entity;

    public RodAimbot() {
        super("RodAimbot", Module.category.combat, 0);
        this.registerSetting(fov = new SliderSetting("FOV", 180, 30, 360, 4));
        this.registerSetting(predicatedTicks = new SliderSetting("Predicted ticks", 5.0, 0.0, 20.0, 1.0));
        this.registerSetting(distance = new SliderSetting("Distance", 6, 3, 30, 0.5));
        this.registerSetting(aimInvis = new ButtonSetting("Aim invis", false));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", false));
    }

    public void onDisable() {
        rotate = false;
        rightClick = false;
        entity = null;
    }

    
    public void onMouse(final MouseEvent mouseEvent) {
        if (mouseEvent.button != 1 || !mouseEvent.buttonstate || !Utils.nullCheck() || mc.currentScreen != null) {
            return;
        }
        if (mc.player.getCurrentEquippedItem() == null || !(mc.player.getCurrentEquippedItem().getItem() instanceof ItemFishingRod) || mc.player.fishEntity != null) {
            return;
        }
        entity = this.getEntity();
        if (entity == null) {
            return;
        }
        mouseEvent.setCanceled(true);
        rightClick = true;
        rotate = true;
    }

    
    public void onPreMotion(PreMotionEvent event) {
        if (!Utils.nullCheck() {
            return;
        }
        if (rightClick || rotate) {
            if (mc.player.getCurrentEquippedItem() == null || !(mc.player.getCurrentEquippedItem().getItem() instanceof ItemFishingRod) {
                return;
            }
            float[] rotations = RotationUtils.getRotationsPredicated(entity, (int)predicatedTicks.getInput());
            if (rotations == null) {
                return;
            }
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            if (!rightClick && rotate) {
                rotate = false;
            }
            if (rightClick) {
                ((IAccessorMinecraft) mc).callRightClickMouse();
                rightClick = false;
            }
        }
    }

    private PlayerEntity getEntity() {
        for (final PlayerEntity entityPlayer : mc.world.getPlayers() {
            if (entityPlayer != mc.player) {
                if (entityPlayer.deathTime != 0) {
                    continue;
                }
                if (!aimInvis.isToggled() && entityPlayer.isInvisible() {
                    continue;
                }
                if (mc.player.getDistanceSqToEntity(entityPlayer) > distance.getInput() * distance.getInput() {
                    continue;
                }
                if (Utils.isFriended(entityPlayer) {
                    continue;
                }
                final float n = (float)fov.getInput();
                if (n != 360.0f && !Utils.inFov(n, entityPlayer) {
                    continue;
                }
                if (AntiBot.isBot(entityPlayer) {
                    continue;
                }
                if (ignoreTeammates.isToggled() && Utils.isTeammate(entityPlayer) {
                    continue;
                }
                return entityPlayer;
            }
        }
        return null;
    }
}
