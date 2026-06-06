package keystrokesmod.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3dd;

public class PreAttackEvent extends Event {
    private Entity target;
    private Vec3dd hitVec;
    private boolean reachModified;
    public PreAttackEvent(Entity target) { this.target = target; }
    public Entity getTarget() { return target; }
    public void setTarget(Entity target) { this.target = target; }
    public Vec3dd getHitVec() { return hitVec; }
    public void setHitVec(Vec3dd hitVec) { this.hitVec = hitVec; }
    public boolean isReachModified() { return reachModified; }
    public void setReachModified(boolean b) { this.reachModified = b; }
}
