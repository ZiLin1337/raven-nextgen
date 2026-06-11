package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitiesDestroyS2CPacket.class)
public class MixinEntitiesDestroyS2CPacket {
    @Inject(method = "getEntityIds", at = @At("HEAD"))
    private void onGetEntityIds(CallbackInfo ci) {
        // 获取实体ID
        // 可用于VelocityFix模块
    }
}
