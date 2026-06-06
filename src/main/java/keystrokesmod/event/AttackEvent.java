package keystrokesmod.event;

import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
    private Entity target;
    public AttackEvent(Entity target) { this.target = target; }
    public Entity getTarget() { return target; }
    public void setTarget(Entity target) { this.target = target; }
}
