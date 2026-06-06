package keystrokesmod.mixin.impl.accessor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface IAccessorNetworkManager {
    @Accessor("ticksSinceLastSync")
    int getTicksSinceLastSync();

    @Accessor("ticksSinceLastSync")
    void setTicksSinceLastSync(int ticks);
}