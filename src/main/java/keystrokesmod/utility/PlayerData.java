package keystrokesmod.utility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;

public class PlayerData {
    public double speed;
    public int aboveVoidTicks;
    public int fastTick;
    public int autoBlockTicks;
    public int ticksExisted;
    public int lastSneakTick;
    public double posZ;
    public int sneakTicks;
    public int noSlowTicks;
    public double posY;
    public boolean sneaking;
    public double posX;
    public double serverPosX;
    public double serverPosY;
    public double serverPosZ;

    public void update(PlayerEntity entityPlayer) {
        final int ticksExisted = entityPlayer.ticksExisted;
        this.posX = entityPlayer.posX - entityPlayer.lastTickPosX;
        this.posY = entityPlayer.posY - entityPlayer.lastTickPosY;
        this.posZ = entityPlayer.posZ - entityPlayer.lastTickPosZ;
        this.speed = Math.max(Math.abs(this.posX), Math.abs(this.posZ));
        if (this.speed >= 0.07) {
            ++this.fastTick;
            this.ticksExisted = ticksExisted;
        }
        else {
            this.fastTick = 0;
        }
        if (Math.abs(this.posY) >= 0.1) {
            this.aboveVoidTicks = ticksExisted;
        }
        if (entityPlayer.isSneaking() {
            this.lastSneakTick = ticksExisted;
        }
        if (entityPlayer.isSwingInProgress && entityPlayer.isBlocking() {
            ++this.autoBlockTicks;
        }
        else {
            this.autoBlockTicks = 0;
        }
        if (entityPlayer.isSprinting() && entityPlayer.isUsingItem() {
            ++this.noSlowTicks;
        }
        else {
            this.noSlowTicks = 0;
        }
        if (entityPlayer.rotationPitch >= 70.0f && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() instanceof BlockItem) {
            if (entityPlayer.swingProgressInt == 1) {
                if (!this.sneaking && entityPlayer.isSneaking() {
                    ++this.sneakTicks;
                }
                else {
                    this.sneakTicks = 0;
                }
            }
        }
        else {
            this.sneakTicks = 0;
        }
    }

    public void updateSneak(final PlayerEntity entityPlayer) {
        this.sneaking = entityPlayer.isSneaking();
    }

    public void updateServerPos(PlayerEntity entityPlayer) {
        this.serverPosX = entityPlayer.serverPosX / 32;
        this.serverPosY = entityPlayer.serverPosY / 32;
        this.serverPosZ = entityPlayer.serverPosZ / 32;
    }
}
