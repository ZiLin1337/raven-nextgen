package keystrokesmod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackEntityEvent extends Event {
    private final PlayerEntity player;
    private final Entity target;
    
    public AttackEntityEvent(PlayerEntity player, Entity target) {
        this.player = player;
        this.target = target;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    public Entity getTarget() {
        return target;
    }
}