package keystrokesmod.mixin.impl.network;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInteractBlockC2SPacket.class)
public class MixinPlayerInteractBlockC2SPacket {
    // KillAura/Reach - 玩家交互方块包
}
