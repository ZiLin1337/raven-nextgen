package keystrokesmod.mixin.impl.entity;

import com.mojang.authlib.GameProfile;
import keystrokesmod.Raven;
import keystrokesmod.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayerEntity {

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;
    @Shadow public net.minecraft.client.input.Input input;

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    @Unique private boolean raven$cancelSend = false;

    public MixinEntityPlayerSP(net.minecraft.client.world.ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onUpdatePre(CallbackInfo c) {
        Raven.EVENT_BUS.post(new PreUpdateEvent());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onUpdatePost(CallbackInfo c) {
        Raven.EVENT_BUS.post(new PostUpdateEvent());
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPacketsHead(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        PreMotionEvent pre = new PreMotionEvent(self.getX(), self.getY(), self.getZ(), self.getYaw(), self.getPitch(), self.isOnGround(), self.isSprinting(), self.isSneaking());
        Raven.EVENT_BUS.post(pre);
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"))
    private void onSendMovementPacketsReturn(CallbackInfo ci) {
        Raven.EVENT_BUS.post(new PostMotionEvent());
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) { }
}
