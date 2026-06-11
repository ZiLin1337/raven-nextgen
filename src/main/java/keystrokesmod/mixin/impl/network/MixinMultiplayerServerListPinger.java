package keystrokesmod.mixin.impl.network;

import net.minecraft.client.network.MultiplayerServerListPinger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiplayerServerListPinger.class)
public class MixinMultiplayerServerListPinger {
    // PingSpoof - 服务器列表Ping
}
