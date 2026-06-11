package keystrokesmod.mixin.impl.network;

import keystrokesmod.Raven;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {
    @Inject(method = "readVarInt", at = @At("HEAD"))
    private void onReadVarInt(CallbackInfo ci) {
        // 读取VarInt
        // 可用于PacketLogger模块
    }
}
