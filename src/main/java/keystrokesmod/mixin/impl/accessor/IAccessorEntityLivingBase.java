package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface IAccessorEntityLivingBase {
    @Accessor("jumping")
    boolean getJumping();
    @Accessor("jumping")
    void setJumping(boolean jumping);
}