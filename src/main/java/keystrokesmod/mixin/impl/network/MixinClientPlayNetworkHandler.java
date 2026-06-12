package keystrokesmod.mixin.impl.network;

import keystrokesmod.utility.ServerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(CallbackInfo ci) {
        // 游戏消息
    }
    
    @Inject(method = "onHealthUpdate", at = @At("RETURN"))
    private void onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            ServerUtils.onHealthUpdate(packet.getHealth(), packet.getFood(), packet.getSaturation());
        }
    }
    
    @Inject(method = "onScoreboardScoreUpdate", at = @At("RETURN"))
    private void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet, CallbackInfo ci) {
        ServerUtils.onScoreboardScore(packet);
    }
}
