package keystrokesmod.event;

import net.minecraft.entity.Entity;
public class PostAttackEvent {
    private Entity target;
    public PostAttackEvent(Entity t) { this.target = t; }
    public Entity getTarget() { return target; }
}