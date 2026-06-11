package keystrokesmod.mixin.impl.network;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInteractItemC2SPacket.class)
public class MixinPlayerInteractItemC2SPacket {
    // KillAura/Reach - 玩家交互物品包
}
