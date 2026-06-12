package keystrokesmod.mixin.impl.gui;

import keystrokesmod.Raven;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // HUD渲染事件
        // 可用于绘制自定义HUD元素
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // 准星渲染
        // 可用于隐藏/修改准星
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(CallbackInfo ci) {
        // 快捷栏渲染
        // 可用于隐藏/修改快捷栏
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusBars(CallbackInfo ci) {
        // 状态栏渲染
        // 可用于隐藏血条/饥饿条
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void onRenderExperienceBar(CallbackInfo ci) {
        // 经验条渲染
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void onRenderExperienceLevel(CallbackInfo ci) {
        // 经验等级渲染
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboard(CallbackInfo ci) {
        // 计分板渲染
        // 可用于隐藏计分板
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderOverlay(CallbackInfo ci) {
        // 叠加层渲染 (南瓜/细雪)
        // 可用于NoRender模块
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffects(CallbackInfo ci) {
        // 状态效果渲染
    }
}
