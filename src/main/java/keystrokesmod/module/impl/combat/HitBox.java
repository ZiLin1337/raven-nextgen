package keystrokesmod.module.impl.combat;

import keystrokesmod.mixin.impl.accessor.IAccessorMinecraft;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class HitBox extends Module {
    public static SliderSetting multiplier;
    public ButtonSetting showHitbox;
    public ButtonSetting playersOnly;
    public ButtonSetting weaponOnly;
    private Entity pointedEntity;
    private HitResult mv;

    public HitBox() {
        super("Hitboxes", category.combat, 0);
        this.registerSetting(multiplier = new SliderSetting("Multiplier", "x", 1.2, 1.0, 5.0, 0.05));
        this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
        this.registerSetting(showHitbox = new ButtonSetting("Show new hitbox", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.closetModule = true;
    }

    @Override
    public String getInfo() {
        return ((int) multiplier.getInput() == multiplier.getInput() ? (int) multiplier.getInput() + "" : multiplier.getInput()) + multiplier.getSuffix();
    }

    
    // TODO: Replace MouseEvent
    public void onMouse(Object e) {
        if (e.button != 0 || !e.buttonstate || !Utils.nullCheck() || multiplier.getInput() == 1 || mc.player.isBlocking() || mc.currentScreen != null) {
            return;
        }
        if (weaponOnly.isToggled() && !Utils.holdingWeapon()) {
            return;
        }
        Entity c = getEntity(1.0F);
        if (c == null) {
            return;
        }
        if (c instanceof PlayerEntity) {
            if (Utils.isFriended((PlayerEntity) c)) {
                return;
            }
        }
        else if (playersOnly.isToggled()) {
            return;
        }
        mc.crosshairTarget = mv;
    }

    
    public void onRenderWorld(Object e) {
        if (showHitbox.isToggled() && Utils.nullCheck()) {
            for (Entity en : mc.world.loadedEntityList) {
                if (en != mc.player && en instanceof LivingEntity && ((LivingEntity) en).deathTime == 0 && !(en instanceof ArmorStandEntity) && !en.isInvisible()) {
                    this.rh(en, Color.WHITE);
                }
            }
        }
    }

    public static double getExpand(Entity en) {
        return multiplier.getInput();
    }

    public Entity getEntity(float partialTicks) {
        if (mc.getRenderViewEntity() != null && mc.world != null) {
            mc.pointedEntity = null;
            pointedEntity = null;
            double d0 = mc.interactionManager.extendedReach() ? 6.0 : (ModuleManager.reach.isEnabled() ? Utils.getRandomValue(Reach.min, Reach.max, Utils.getRandom()) : 3.0);
            mv = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
            double d2 = d0;
            Vec3d vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);

            if (mv != null) {
                d2 = mv.hitVec.distanceTo(vec3);
            }

            Vec3d vec4 = mc.getRenderViewEntity().getLook(partialTicks);
            Vec3d vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            Vec3d vec6 = null;
            float f1 = 1.0F;
            List list = mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand((double) f1, (double) f1, (double) f1));
            double d3 = d2;

            for (Object o : list) {
                Entity entity = (Entity) o;
                if (entity.canBeCollidedWith()) {
                    float ex = (float) ((double) entity.getCollisionBorderSize() * getExpand(entity));
                    Box ax = entity.getEntityBoundingBox().expand((double) ex, (double) ex, (double) ex);
                    HitResult mop = ax.calculateIntercept(vec3, vec5);
                    if (ax.isVecInside(vec3)) {
                        if (0.0D < d3 || d3 == 0.0D) {
                            pointedEntity = entity;
                            vec6 = mop == null ? vec3 : mop.hitVec;
                            d3 = 0.0D;
                        }
                    } else if (mop != null) {
                        double d4 = vec3.distanceTo(mop.hitVec);
                        if (d4 < d3 || d3 == 0.0D) {
                            if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                                if (d3 == 0.0D) {
                                    pointedEntity = entity;
                                    vec6 = mop.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                vec6 = mop.hitVec;
                                d3 = d4;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (d3 < d2 || mv == null)) {
                mv = new MovingObjectPosition(pointedEntity, vec6);
                if (pointedEntity instanceof LivingEntity || pointedEntity instanceof ItemFrameEntity) {
                    return pointedEntity;
                }
            }
        }
        return null;
    }

    private void rh(Entity e, Color c) {
        if (e instanceof LivingEntity) {
            float partialTicks = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) partialTicks - mc.getEntityRenderDispatcher().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) partialTicks - mc.getEntityRenderDispatcher().viewerPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) partialTicks - mc.getEntityRenderDispatcher().viewerPosZ;
            float ex = (float) ((double) e.getCollisionBorderSize() * multiplier.getInput());
            Box bbox = e.getEntityBoundingBox().expand((double) ex, (double) ex, (double) ex);
            Box axis = new Box(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            GL11.glColor3d((double) c.getRed(), (double) c.getGreen(), (double) c.getBlue());
            WorldRenderer.drawSelectionBoundingBox(axis);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }
}
