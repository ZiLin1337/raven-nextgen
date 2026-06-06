package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import java.awt.Color;

public class PlayerHUD extends Module {
    private static final MinecraftClient mc = mc;
    private static final String[] COLOR_MODES = {"Static", "Gradient", "Rainbow"};
    private static final String[] OUTLINE_MODES = {"None", "Full", "Side"};

    public static SliderSetting colorMode;
    public static ColorSetting hudColor;
    public static ColorSetting hudColor2;
    public static SliderSetting fontSize;
    public static SliderSetting outline;
    public static ButtonSetting alphabeticalSort;
    public static ButtonSetting drawBackground;
    public static ButtonSetting textShadow;
    public static ButtonSetting alignRight;
    public static ButtonSetting lowercase;
    public static ButtonSetting showInfo;

    public static float posX = 5.0f;
    public static float posY = 70.0f;

    public PlayerHUD() {
        super("HUD", Module.category.render);
        this.registerSetting(colorMode = new SliderSetting("Color mode", 0, COLOR_MODES));
        this.registerSetting(hudColor = new ColorSetting("Color", 255, 255, 255));
        this.registerSetting(hudColor2 = new ColorSetting("Color 2", 85, 85, 255));
        this.registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        this.registerSetting(outline = new SliderSetting("Outline", 0, OUTLINE_MODES));
        this.registerSetting(alignRight = new ButtonSetting("Align right", false));
        this.registerSetting(alphabeticalSort = new ButtonSetting("Alphabetical sort", false));
        this.registerSetting(drawBackground = new ButtonSetting("Draw background", false));
        this.registerSetting(textShadow = new ButtonSetting("Text shadow", true));
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false));
        this.registerSetting(showInfo = new ButtonSetting("Show module info", true));
    }

    @Override
    public void guiUpdate() {
        int mode = (int) colorMode.getInput();
        hudColor.setVisible(mode == 0 || mode == 1, this);
        hudColor2.setVisible(mode == 1, this);
    }

    @Override
    public void onEnable() { ModuleManager.sort(); }

    public static void renderHud(DrawContext context) {
        if (mc.currentScreen != null || mc.options.hudHidden) return;

        float yPos = posY;
        int longest = getLongestModule();
        boolean removeVelocity = ModuleManager.antiKnockback != null && ModuleManager.antiKnockback.isEnabled();

        for (Module module : ModuleManager.organizedModules) {
            if (!module.isEnabled() || module.isHidden() || shouldSkipModule(module, removeVelocity)) continue;

            String name = getHudRenderText(module);
            int width = mc.textRenderer.getWidth(name);
            float x = alignRight.isToggled() ? posX - width : posX;

            int color = getHudColor((int) (yPos * 2));
            if (drawBackground.isToggled()) {
                context.fill((int) x - 2, (int) yPos - 1, (int) x + width + 2, (int) yPos + 10, new Color(0, 0, 0, 110).getRGB());
            }

            if (outline.getInput() > 0) {
                context.fill((int) x - 3, (int) yPos - 2, (int) x - 2, (int) yPos + 11, color);
                if (outline.getInput() == 1) {
                    context.fill((int) x + width + 2, (int) yPos - 2, (int) x + width + 3, (int) yPos + 11, color);
                }
            }

            context.drawTextWithShadow(mc.textRenderer, name, (int) x, (int) yPos, color);
            yPos += 11;
        }
    }

    private static boolean shouldSkipModule(Module module, boolean removeVelocity) {
        if (module instanceof HUD) return true;
        return false;
    }

    private static int getLongestModule() {
        int length = 0;
        for (Module module : ModuleManager.organizedModules) {
            if (module.isEnabled() && !module.isHidden()) {
                length = Math.max(length, mc.textRenderer.getWidth(getHudRenderText(module)));
            }
        }
        return length;
    }

    public static String getHudRenderText(Module module) {
        String name = module.getName();
        if (lowercase.isToggled()) name = name.toLowerCase();
        if (showInfo.isToggled() && !module.getInfo().isEmpty()) name += " \u00a77" + module.getInfo();
        return name;
    }

    public static int getHudColor(int phase) {
        int mode = (int) colorMode.getInput();
        if (mode == 2) { // Rainbow
            float hue = (float) (System.currentTimeMillis() % 7500L) / 7500.0f;
            return Color.HSBtoRGB(hue, 1.0f, 1.0f);
        }
        if (mode == 1) { // Gradient
            float t = (float) (phase % 100) / 100.0f;
            return blendColors(hudColor.getColor(), hudColor2.getColor(), t);
        }
        return hudColor.getColor();
    }

    private static int blendColors(int c1, int c2, float t) {
        int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }
}
