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
    }

    public void updateSneak(final PlayerEntity entityPlayer) {
    }

    public void updateServerPos(PlayerEntity entityPlayer) {
    }
}
