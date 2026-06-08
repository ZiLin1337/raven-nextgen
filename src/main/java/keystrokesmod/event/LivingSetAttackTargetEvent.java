package keystrokesmod.event;

import net.minecraft.entity.LivingEntity;

public class LivingSetAttackTargetEvent extends Event {
    public final LivingEntity entity;
    public final LivingEntity target;

    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target) {
        this.entity = entity;
        this.target = target;
    }
}
