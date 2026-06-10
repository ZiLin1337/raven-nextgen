package keystrokesmod.mixin.impl.network;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

// 1.21.4中ModList工作方式已变化，保留为骨架
@Mixin(ClientConnection.class)
public class MixinModList {
}
