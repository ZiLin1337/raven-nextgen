package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface IAccessorEntity {
    @Accessor("fire")
    int getFire();
    @Accessor("fire")
    void setFire(int fire);

    @Accessor("nextStepDistance")
    int getNextStepDistance();
    @Accessor("nextStepDistance")
    void setNextStepDistance(int nextStepDistance);

    @Accessor("isInWeb")
    boolean getIsInWeb();
    @Accessor("isInWeb")
    void setIsInWeb(boolean isInWeb);

    @Accessor("air")
    int getAir();
    @Accessor("air")
    void setAir(int air);

    @Accessor("lateral")
    boolean getLateral();
    @Accessor("lateral")
    void setLateral(boolean lateral);
}