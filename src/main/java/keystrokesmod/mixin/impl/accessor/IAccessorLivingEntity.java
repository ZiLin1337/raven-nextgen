package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.damage.DamageSource;

public interface IAccessorLivingEntity {
    default int getInvulnerableTime() { return 0; }
    default void setInvulnerableTime(int invulnerableTime) {}
    default float getLastDamageTaken() { return 0.0F; }
    default void setLastDamageTaken(float lastDamageTaken) {}
    default int getAir() { return 0; }
    default void setAir(int air) {}
    default DamageSource getLastDamageSrc() { return null; }
    default void setLastDamageSrc(DamageSource source) {}
}
