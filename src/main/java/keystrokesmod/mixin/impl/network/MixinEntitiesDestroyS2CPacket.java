package keystrokesmod.mixin.impl.network;

import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntitiesDestroyS2CPacket.class)
public class MixinEntitiesDestroyS2CPacket {
    // Velocity/VelocityFix - 实体销毁包
}
