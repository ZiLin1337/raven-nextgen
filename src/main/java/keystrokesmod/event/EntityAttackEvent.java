package keystrokesmod.event;

import net.minecraft.entity.Entity;

public class EntityAttackEvent extends AttackEvent {
    public EntityAttackEvent(Entity target) {
        super(target);
    }
}
