package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface IAccessorEntityLivingBase {
    @Accessor("invulnerableTime")
    int getInvulnerableTime();
    @Accessor("invulnerableTime")
    void setInvulnerableTime(int invulnerableTime);

    @Accessor("lastDamageTaken")
    float getLastDamageTaken();
    @Accessor("lastDamageTaken")
    void setLastDamageTaken(float lastDamageTaken);

    @Accessor("air")
    int getAir();

    @Accessor("air")
    void setAir(int air);

    @Accessor("lastDamageSrc")
    DamageSource getLastDamageSrc();
    @Accessor("lastDamageSrc")
    void setLastDamageSrc(DamageSource source);
}