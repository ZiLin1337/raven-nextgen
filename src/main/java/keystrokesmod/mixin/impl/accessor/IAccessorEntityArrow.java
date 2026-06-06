package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArrowEntity.class)
public interface IAccessorEntityArrow {
    @Accessor("life")
    int getLife();
    @Accessor("life")
    void setLife(int life);

    @Accessor("accelerationX")
    double getAccelerationX();
    @Accessor("accelerationX")
    void setAccelerationX(double accelerationX);

    @Accessor("accelerationY")
    double getAccelerationY();
    @Accessor("accelerationY")
    void setAccelerationY(double accelerationY);

    @Accessor("accelerationZ")
    double getAccelerationZ();
    @Accessor("accelerationZ")
    void setAccelerationZ(double accelerationZ);
}