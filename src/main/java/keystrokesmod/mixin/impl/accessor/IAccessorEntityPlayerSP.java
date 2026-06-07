package keystrokesmod.mixin.impl.accessor;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface IAccessorEntityPlayerSP {
    @Accessor("lastX")
    double getLastX();

    @Accessor("lastY")
    double getLastY();

    @Accessor("lastZ")
    double getLastZ();

    @Accessor("lastYaw")
    float getLastYaw();

    @Accessor("lastPitch")
    float getLastPitch();
}