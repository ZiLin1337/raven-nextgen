package keystrokesmod.mixin.impl.network;

import org.spongepowered.asm.mixin.Mixin;

// 1.21.4中FMLHandshakeMessage不存在
// 此mixin在Fabric环境下为兼容桩
@Mixin(net.minecraft.network.ClientConnection.class)
public class MixinModList {
}
