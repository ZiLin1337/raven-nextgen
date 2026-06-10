package keystrokesmod.mixin.impl.accessor;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConnection.class)
public interface IAccessorNetworkManager {
}