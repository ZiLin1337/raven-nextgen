package keystrokesmod.script.model;

import keystrokesmod.Raven;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class Entity {
    public net.minecraft.entity.Entity entity;
    public Vec3 position;
    public Vec3 lastPosition;
    public boolean isPlayer;
    public boolean isLiving;
    public String type;
    public int entityId;

    public Entity(net.minecraft.entity.Entity entity) {
        this.entity = entity;
        this.entityId = entity.getId();
        this.isPlayer = entity instanceof PlayerEntity;
        this.isLiving = entity instanceof LivingEntity;
        this.type = entity.getType().getName().getString();
        this.position = Vec3.convert(entity.getPos());
        this.lastPosition = Vec3.convert(entity.prevX, entity.prevY, entity.prevZ);
    }

    public Vec3 getPosition() { return Vec3.convert(entity.getPos()); }
    public Vec3 getLastPosition() { return Vec3.convert(entity.prevX, entity.prevY, entity.prevZ); }
    public Vec3 getServerPosition() { return new Vec3(entity.getX(), entity.getY(), entity.getZ()); }
    public boolean isOnGround() { return entity.isOnGround(); }
    public boolean isDead() { return !entity.isAlive(); }

    public float getHealth() {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() : 0;
    }

    public float getMaxHealth() {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).getMaxHealth() : 0;
    }

    public float getAbsorption() {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).getAbsorptionAmount() : 0;
    }

    public int getHurtTime() {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).hurtTime : 0;
    }

    public int getMaxHurtTime() {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).maxHurtTime : 0;
    }

    public float getYaw() { return entity.getYaw(); }
    public float getPitch() { return entity.getPitch(); }

    public Box getBoundingBox() { return entity.getBoundingBox(); }

    public ItemStack getHeldItem() {
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getMainHandStack();
        }
        return ItemStack.EMPTY;
    }

    public boolean isHoldingBlock() {
        return getHeldItem().getItem() instanceof net.minecraft.item.BlockItem;
    }

    public boolean isHoldingWeapon() {
        return Utils.holdingWeapon();
    }

    public String getName() { return entity.getName().getString(); }

    public boolean isInLiquid() { return entity.isTouchingWater() || entity.isInLava(); }
    public boolean isCollidedHorizontally() { return entity.horizontalCollision; }
    public boolean isCollidedVertically() { return entity.verticalCollision; }
    public boolean isOnLadder() { return entity instanceof LivingEntity && ((LivingEntity) entity).isClimbing(); }

    public double getMotionX() { return entity.getVelocity().x; }
    public double getMotionY() { return entity.getVelocity().y; }
    public double getMotionZ() { return entity.getVelocity().z; }

    public Vec3 getMotion() { return Vec3.convert(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z); }

    public float distanceTo(Entity other) {
        return entity.distanceTo(other.entity);
    }

    public double getDistanceSqToEntity(Entity other) {
        return entity.squaredDistanceTo(other.entity);
    }

    public NetworkPlayer getNetworkPlayer() {
        if (entity instanceof PlayerEntity) {
            return NetworkPlayer.convert(Raven.mc.getNetworkHandler().getPlayerListEntry(entity.getUuid()));
        }
        return null;
    }

    public boolean isInvisible() { return entity.isInvisible(); }
    public boolean isSneaking() { return entity.isSneaking(); }
    public boolean isSprinting() { return entity.isSprinting(); }

    @Override
    public String toString() {
        return "Entity(" + type + ")";
    }
}
