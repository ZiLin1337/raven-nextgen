package keystrokesmod.mixin.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.NameHider;
import keystrokesmod.module.impl.render.MobESP;
import keystrokesmod.module.impl.render.PlayerESP;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntityEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity> {
    @Shadow protected boolean renderOutlines;
    @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Unique private LivingEntity nameHider$entity;
    @Unique private LivingEntity damageTint$entity;

    @ModifyVariable(method = "render", at = @At("STORE", ordinal = 0), ordinal = 0)
    private boolean modifyInvisibleFlag(boolean flag, T entity) {
        return flag || (this.renderOutlines && shouldRender() && ModuleManager.playerESP.showInvis.isToggled());
    }

    @Unique
    private boolean shouldRender() {
        return ModuleManager.playerESP != null && ModuleManager.playerESP.isEnabled() && ModuleManager.playerESP.outline.isToggled();
    }

    @Inject(method = "labelHeight", at = @At("HEAD"), cancellable = true)
    private void suppressNameDuringOutlinePass(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerESP.renderingOutlinePass || MobESP.renderingOutlinePass) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private int raven$getOutlineColor(LivingEntity entity) {
        int i = 0xFFFFFF;
        boolean drawOutline = shouldRender()
                && (entity != MinecraftClient.getInstance().player || ModuleManager.playerESP.renderSelf.isToggled());
        if (drawOutline && !AntiBot.isBot(entity)) {
            if (ModuleManager.playerESP.rainbow.isToggled()) {
                i = Utils.getChroma(2L, 0L);
            } else {
                i = ModuleManager.playerESP.color.getColor();
            }
            if (ModuleManager.playerESP.redOnDamage.isToggled() && entity.hurtTime != 0) {
                i = Color.RED.getRGB();
            }
            return i;
        }
        return -1;
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;DDDFF)V", at = @At("HEAD"))
    private void mobEsp$renderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity)) {
            MobESP.onRenderMobPre(entity);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;DDDFF)V", at = @At("RETURN"))
    private void mobEsp$renderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity)) {
            MobESP.onRenderMobPost();
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"))
    private void nameHider$captureEntity(T entity, Text text, CallbackInfo ci) {
        this.nameHider$entity = entity;
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 0)
    private Text nameHider$hideName(Text text) {
        if (text == null || ModuleManager.nameHider == null || !ModuleManager.nameHider.isEnabled()) {
            return text;
        }
        if (this.nameHider$entity instanceof PlayerEntity) {
            return NameHider.getPlayerDisplayName(
                    (PlayerEntity) this.nameHider$entity,
                    this.nameHider$entity.getDisplayName()
            );
        }
        return Text.literal(NameHider.getFakeName(text.getString());
    }

    @Inject(method = "renderLabelIfPresent", at = @At("RETURN"))
    private void nameHider$clearEntity(T entity, Text text, CallbackInfo ci) {
        this.nameHider$entity = null;
    }
}