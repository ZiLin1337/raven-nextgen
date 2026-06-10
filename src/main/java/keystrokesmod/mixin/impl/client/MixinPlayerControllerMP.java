package keystrokesmod.mixin.impl.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinPlayerControllerMP {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) { }

    @Inject(method = "attackEntity", at = @At("RETURN"))
    private void onPostAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) { }
}
