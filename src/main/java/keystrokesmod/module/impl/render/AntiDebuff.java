package keystrokesmod.module.impl.render;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import java.util.*;

public class AntiDebuff extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final SliderSetting mode;
    private final ButtonSetting showOverlay, showWarning;

    public AntiDebuff() {
        super("AntiDebuff", category.render);
        registerSetting(mode = new SliderSetting("Mode", 0, 0, 2, 1));
        registerSetting(showOverlay = new ButtonSetting("Show overlay", true));
        registerSetting(showWarning = new ButtonSetting("Show warning", true));
    }

    @EventHandler
    public void onRender2D(Render2DEvent e) {
        if (!Utils.nullCheck() || mc.player == null) return;
        DrawContext ctx = e.getContext();
        boolean hasDebuff = false;
        for (StatusEffectInstance effect : mc.player.getStatusEffects() {
            if (isDebuff(effect) {
                hasDebuff = true;
                break;
            }
        }
        if (hasDebuff && showOverlay.isToggled() {
            ctx.fill(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0x40FF0000);
        }
        if (hasDebuff && showWarning.isToggled() {
            String warning = "DEBUFF ACTIVE!";
            int x = mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(warning) / 2;
            int y = mc.getWindow().getScaledHeight() / 2 - 20;
            ctx.drawTextWithShadow(mc.textRenderer, warning, x, y, 0xFFFF0000);
        }
    }

    private boolean isDebuff(StatusEffectInstance effect) {
        return effect.getEffectType() == StatusEffects.POISON ||
               effect.getEffectType() == StatusEffects.WITHER ||
               effect.getEffectType() == StatusEffects.MINING_FATIGUE ||
               effect.getEffectType() == StatusEffects.NAUSEA ||
               effect.getEffectType() == StatusEffects.BLINDNESS ||
               effect.getEffectType() == StatusEffects.HUNGER ||
               effect.getEffectType() == StatusEffects.SLOWNESS ||
               effect.getEffectType() == StatusEffects.WEAKNESS;
    }
}
