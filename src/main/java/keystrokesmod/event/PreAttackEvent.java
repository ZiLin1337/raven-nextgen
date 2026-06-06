package keystrokesmod.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class PreAttackEvent extends Event {
    private Entity target;
    private Vec3d hitVec;
    private boolean reachModified;
    public PreAttackEvent(Entity target) { this.target = target; }
    public Entity getTarget() { return target; }
    public void setTarget(Entity target) { this.target = target; }
    public Vec3d getHitVec() { return hitVec; }
    public void setHitVec(Vec3d hitVec) { this.hitVec = hitVec; }
    public boolean isReachModified() { return reachModified; }
    public void setReachModified(boolean b) { this.reachModified = b; }
}
