package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.awt.Color;
import java.util.*;

/**
 * HUD Module - Full recreation of original 801-line implementation.
 * Features: color modes (static/gradient/rainbow), wave effects, outline modes,
 * font selection, drag-to-move editor, module list rendering.
 */
public class HUD extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // === Color Modes ===
    private static final String[] COLOR_MODES = {"Static", "Gradient", "Rainbow"};
    private static final String[] WAVE_AXES = {"Vertical", "Horizontal"};
    private static final String[] VERTICAL_WAVE_DIRECTIONS = {"Down", "Up"};
    private static final String[] HORIZONTAL_WAVE_DIRECTIONS = {"Left", "Right"};
    private static final String[] OUTLINE_MODES = {"None", "Full", "Side"};

    // === Wave Constants ===
    private static final double HUD_WAVE_HORIZONTAL_X_SCALE = 0.35;
    private static final long HUD_RAINBOW_PERIOD_MS = 7500L;
    private static final double HUD_WAVE_ANGLE_SCALE = 0.12;

    // === Rendering Constants ===
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 110).getRGB();
    private static final float DEFAULT_POS_X = 5.0f;
    private static final float DEFAULT_POS_Y = 70.0f;

    // === Settings ===
    public static SliderSetting colorMode;
    public static SliderSetting hudColor, hudColor2;
    public static SliderSetting waveAxis, verticalWaveDirection, horizontalWaveDirection;
    public static SliderSetting waveSpeed, waveLength;
    public static SliderSetting font, fontSize, outline;
    public static ButtonSetting alphabeticalSort, drawBackground, textShadow, alignRight, lowercase, showInfo;

    // === Position ===
    public static float posX = DEFAULT_POS_X, posY = DEFAULT_POS_Y;
    private static float relativePosX = Float.NaN, relativePosY = Float.NaN;

    // === Runtime State ===
    private boolean isAlphabeticalSort, canShowInfo;
    private String lastHudFontName = "";
    private float lastHudFontScale = -1.0f;

    public HUD() {
        super("HUD", category.render);
        // Color settings
        registerSetting(colorMode = new SliderSetting("Color mode", 0, COLOR_MODES));
        registerSetting(hudColor = new SliderSetting("Color", 0xFFFFFF, 0, 0xFFFFFF, 1));
        registerSetting(hudColor2 = new SliderSetting("Color 2", 0x5555FF, 0, 0xFFFFFF, 1));

        // Wave settings
        registerSetting(waveAxis = new SliderSetting("Wave axis", 0, WAVE_AXES));
        registerSetting(verticalWaveDirection = new SliderSetting("Wave direction", 0, VERTICAL_WAVE_DIRECTIONS));
        registerSetting(horizontalWaveDirection = new SliderSetting("Wave direction", 0, HORIZONTAL_WAVE_DIRECTIONS));
        registerSetting(waveSpeed = new SliderSetting("Wave speed", 1.0, 0.1, 5.0, 0.1));
        registerSetting(waveLength = new SliderSetting("Wave length", 1.0, 0.5, 5.0, 0.1));

        // Font settings
        registerSetting(font = new SliderSetting("Font", 0, 0, 2, 1));
        registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));

        // Outline settings
        registerSetting(outline = new SliderSetting("Outline", 0, OUTLINE_MODES));

        // Action buttons
        registerSetting(new ButtonSetting("Edit position", () -> {
            if (mc.currentScreen == null) mc.setScreen(new EditScreen());
        }));

        // Display options
        registerSetting(alignRight = new ButtonSetting("Align right", false));
        registerSetting(alphabeticalSort = new ButtonSetting("Alphabetical sort", false));
        registerSetting(drawBackground = new ButtonSetting("Draw background", false));
        registerSetting(textShadow = new ButtonSetting("Text shadow", true));
        registerSetting(lowercase = new ButtonSetting("Lowercase", false));
        registerSetting(showInfo = new ButtonSetting("Show module info", true));
    }

    // === Lifecycle Methods ===

    @Override
    public void onEnable() {
        guiUpdate();
        ModuleManager.sort();
    }

    @Override
    public void guiButtonToggled(ButtonSetting btn) {
        if (btn == alphabeticalSort || btn == showInfo) {
            ModuleManager.sort();
        }
    }

    public void guiUpdate() {
        int mode = colorMode == null ? 0 : (int) colorMode.getInput();
        if (hudColor != null) hudColor.setVisible(mode == 0 || mode == 1, this);
        if (hudColor2 != null) hudColor2.setVisible(mode == 1, this);

        boolean showWaveSettings = mode == 1 || mode == 2;
        boolean verticalAxis = hudWaveIsVertical();

        if (waveAxis != null) waveAxis.setVisible(showWaveSettings, this);
        if (verticalWaveDirection != null) verticalWaveDirection.setVisible(showWaveSettings && verticalAxis, this);
        if (horizontalWaveDirection != null) horizontalWaveDirection.setVisible(showWaveSettings && !verticalAxis, this);
        if (waveSpeed != null) waveSpeed.setVisible(showWaveSettings, this);
        if (waveLength != null) waveLength.setVisible(showWaveSettings, this);
    }

    // === Main Render Handler ===

    @EventHandler
    public void onRender2D(keystrokesmod.event.Render2DEvent event) {
        if (!Utils.nullCheck()) return;

        // Check for sort updates
        if (isAlphabeticalSort != alphabeticalSort.isToggled()) {
            isAlphabeticalSort = alphabeticalSort.isToggled();
            ModuleManager.sort();
        }
        if (canShowInfo != showInfo.isToggled()) {
            canShowInfo = showInfo.isToggled();
            ModuleManager.sort();
        }

        String currentFontName = getSelectedFontName();
        float currentFontScale = getSelectedFontScale();
        if (!currentFontName.equals(lastHudFontName) || Float.compare(currentFontScale, lastHudFontScale) != 0) {
            lastHudFontName = currentFontName;
            lastHudFontScale = currentFontScale;
            ModuleManager.sort();
        }

        if (mc.currentScreen != null) return;

        DrawContext ctx = event.getContext();
        syncPositionToResolution();

        // Get font renderer
        RavenFontRenderer hudFont = getHudFontRenderer();

        int textTopOffset = hudFont.getTextTopOffset();
        int textBottomOffset = hudFont.getTextBottomOffset();
        int horizontalTextPadding = getHudHorizontalTextPadding();
        int textTopPadding = getHudTextTopPadding();
        int textBottomPadding = getHudTextBottomPadding();
        int outlineThickness = getHudOutlineThickness();
        int rowHeight = getHudRowHeight(textTopOffset, textBottomOffset, textTopPadding, textBottomPadding);

        float yPos = posY;
        double verticalWaveAccum = 0.0;
        boolean firstVisibleRow = true;
        String previousModule = "";
        double lastOutlineLeft = 0.0;
        double lastOutlineRight = 0.0;
        double lastBackgroundBottom = 0.0;

        try {
            for (Module module : ModuleManager.getModules()) {
                if (!module.isEnabled() || module == this || shouldSkipModule(module)) continue;

                String moduleName = getHudRenderText(module);
                int moduleWidth = hudFont.getStringWidth(moduleName);
                float xPos = posX;
                float textY = getHudTextY(yPos, textTopOffset, textTopPadding);

                double backgroundLeft = xPos - horizontalTextPadding;
                double backgroundRight = xPos + moduleWidth + horizontalTextPadding;
                double backgroundTop = yPos;
                double backgroundBottom = yPos + rowHeight;
                double outlineLeft = backgroundLeft - outlineThickness;
                double outlineRight = backgroundRight + outlineThickness;
                double outlineTop = backgroundTop - outlineThickness;

                if (alignRight.isToggled()) {
                    xPos -= moduleWidth;
                    backgroundLeft = xPos - horizontalTextPadding;
                    backgroundRight = xPos + moduleWidth + horizontalTextPadding;
                    outlineLeft = backgroundLeft - outlineThickness;
                    outlineRight = backgroundRight + outlineThickness;
                }

                double rowCenterX = (backgroundLeft + backgroundRight) * 0.5;
                double wavePhase = hudWavePhase(verticalWaveAccum, rowCenterX);
                int color = getHudColor(wavePhase);

                // Draw background
                if (drawBackground.isToggled()) {
                    drawRect(ctx, backgroundLeft, backgroundTop, backgroundRight, backgroundBottom, BACKGROUND_COLOR);
                }

                // Draw top outline
                if (outline.getInput() == 1 && firstVisibleRow) {
                    drawRect(ctx, outlineLeft, outlineTop, outlineRight, backgroundTop, color);
                }

                if (hudWaveIsVertical()) {
                    verticalWaveAccum += getVerticalWaveStep();
                }
                firstVisibleRow = false;

                // Draw connection to previous module
                if (outline.getInput() == 1 && !previousModule.isEmpty()) {
                    double difference = hudFont.getStringWidth(previousModule) - moduleWidth;
                    if (alphabeticalSort.isToggled() && difference < 0) {
                        drawRect(ctx, outlineLeft, outlineTop, xPos - difference + horizontalTextPadding + outlineThickness, backgroundTop, color);
                    } else if (alignRight.isToggled()) {
                        drawRect(ctx, xPos - difference - horizontalTextPadding - outlineThickness, outlineTop, backgroundLeft, backgroundTop, color);
                    } else {
                        drawRect(ctx, backgroundRight, outlineTop, xPos + difference + moduleWidth + horizontalTextPadding + outlineThickness, backgroundTop, color);
                    }
                }

                // Draw side outlines
                if (outline.getInput() > 0) {
                    if (alignRight.isToggled()) {
                        drawRect(ctx, backgroundRight, backgroundTop, outlineRight, backgroundBottom, color);
                    } else {
                        drawRect(ctx, outlineLeft, backgroundTop, backgroundLeft, backgroundBottom, color);
                    }
                }

                if (outline.getInput() == 1) {
                    if (alignRight.isToggled()) {
                        drawRect(ctx, outlineLeft, backgroundTop, backgroundLeft, backgroundBottom, color);
                    } else {
                        drawRect(ctx, backgroundRight, backgroundTop, outlineRight, backgroundBottom, color);
                    }
                }

                // Draw text
                drawHudText(hudFont, moduleName, xPos, textY, color);

                previousModule = moduleName;
                lastOutlineLeft = outlineLeft;
                lastOutlineRight = outlineRight;
                lastBackgroundBottom = backgroundBottom;
                yPos += rowHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Draw bottom outline
        if (outline.getInput() == 1 && !previousModule.isEmpty()) {
            double bottomCenterX = (lastOutlineLeft + lastOutlineRight) * 0.5;
            double bottomPhase = hudWavePhase(verticalWaveAccum, bottomCenterX);
            drawRect(ctx, lastOutlineLeft, lastBackgroundBottom, lastOutlineRight, lastBackgroundBottom + outlineThickness, getHudColor(bottomPhase));
        }
    }

    // === Helper Methods ===

    private static boolean shouldSkipModule(Module module) {
        return module.isHidden() || module.getName().equals("HUD");
    }

    public static int getLongestModule() {
        RavenFontRenderer hudFont = getHudFontRenderer();
        int length = 0;
        for (Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                length = Math.max(length, hudFont.getStringWidth(getHudRenderText(module)));
            }
        }
        return length;
    }

    

    private static int getHudColor(double phase) {
        int mode = colorMode == null ? 0 : (int) colorMode.getInput();
        if (mode == 0) {
            return hudColor == null ? 0xFFFFFF : (int) hudColor.getInput();
        }
        if (mode == 1) {
            int c1 = hudColor == null ? 0xFFFFFF : (int) hudColor.getInput();
            int c2 = hudColor2 == null ? 0x5555FF : (int) hudColor2.getInput();
            float t = (float) ((Math.sin(phase) + 1) * 0.5);
            return blendColor(c1, c2, t);
        }
        return rainbowColor(phase);
    }

    private static int blendColor(int c1, int c2, float t) {
        int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int rainbowColor(double phase) {
        float hue = (float) ((System.currentTimeMillis() % HUD_RAINBOW_PERIOD_MS) / (double) HUD_RAINBOW_PERIOD_MS + phase * HUD_WAVE_ANGLE_SCALE);
        return Color.HSBtoRGB(hue % 1.0f, 0.7f, 1.0f) | 0xFF000000;
    }

    // === Wave Methods ===

    private static boolean hudWaveIsVertical() {
        return waveAxis == null || (int) waveAxis.getInput() == 0;
    }

    private static double hudWavePhase(double verticalAccum, double rowCenterX) {
        if (hudWaveIsVertical()) {
            return verticalAccum;
        }
        return rowCenterX * (HUD_WAVE_HORIZONTAL_X_SCALE / getWaveLengthMultiplier()) * getHorizontalWaveDirectionSign();
    }

    private static double getVerticalWaveStep() {
        return (12.0 / getWaveLengthMultiplier()) * getVerticalWaveDirectionSign();
    }

    private static int getVerticalWaveDirectionSign() {
        return verticalWaveDirection == null || (int) verticalWaveDirection.getInput() == 0 ? -1 : 1;
    }

    private static int getHorizontalWaveDirectionSign() {
        return horizontalWaveDirection == null || (int) horizontalWaveDirection.getInput() == 0 ? 1 : -1;
    }

    private static double getWaveLengthMultiplier() {
        return waveLength != null ? waveLength.getInput() : 1.0;
    }

    // === Font Methods ===

    

    

    

    private static void drawHudText(RavenFontRenderer hudFont, String moduleName, float xPos, float textY, int color) {
        if (!shouldUseHorizontalWaveText()) {
            hudFont.drawString(moduleName, xPos, textY, color, shouldDrawTextShadow());
            return;
        }
        hudFont.drawGlyphString(moduleName, xPos, textY, (character, xOffset, width, formattingColor) -> {
            if (formattingColor != null) return formattingColor;
            return getHudColor(hudWavePhase(0.0, xPos + xOffset + width * 0.5f));
        }, shouldDrawTextShadow());
    }

    private static boolean shouldUseHorizontalWaveText() {
        return colorMode != null && (int) colorMode.getInput() != 0 && !hudWaveIsVertical();
    }

    // === Drawing Methods ===

    private static void drawRect(DrawContext ctx, double x1, double y1, double x2, double y2, int color) {
        ctx.fill((int) x1, (int) y1, (int) x2, (int) y2, color);
    }

    private static boolean shouldDrawTextShadow() {
        return textShadow == null || textShadow.isToggled();
    }

    // === Position Methods ===

    

    public static float getRelativePosY() {
        syncPositionToResolution();
        return relativePosY;

    // === Font Methods ===

    public static RavenFontRenderer getHudFontRenderer() {
        return FontManager.getHudRenderer(getSelectedFontName(), getSelectedFontScale());
    }

    public static String getHudRenderText(Module module) {
        String moduleName = module.getName();
        if (lowercase != null \&\& lowercase.isToggled()) {
            moduleName = moduleName.toLowerCase();
        }
        return moduleName;
    }

    public static String getSelectedFontName() {
        return FontManager.getHudFontOptions()[(int) font.getInput()];
    }

    public static float getSelectedFontScale() {
        return (float) fontSize.getInput();
    }

    public static float getRelativePosX() {
        syncPositionToResolution();
        return relativePosX;
    }

    public static void setRelativePosition(float normalizedX, float normalizedY) {
        relativePosX = normalizedX;
        relativePosY = normalizedY;
    }

    public static void setAbsolutePosition(float absoluteX, float absoluteY) {
        posX = absoluteX;
        posY = absoluteY;
    }

    public static void resetPosition() {
        setRelativePosition(DEFAULT_RELATIVE_X, DEFAULT_RELATIVE_Y);
    }

    private static void syncPositionToResolution() {
        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        if (Float.isNaN(relativePosX) || Float.isNaN(relativePosY)) {
            if (Float.isNaN(posX) || Float.isNaN(posY)) {
                relativePosX = DEFAULT_RELATIVE_X;
                relativePosY = DEFAULT_RELATIVE_Y;
            }
            else {
                relativePosX = posX / scaledWidth;
                relativePosY = posY / scaledHeight;
            }
        }

        posX = relativePosX * scaledWidth;
        posY = relativePosY * scaledHeight;
    }

    public static int getHudHorizontalTextPadding() { return getScaledHudPixels(2.0f); }
    private static int getHudTextTopPadding() { return getScaledHudPixels(2.0f); }
    private static int getHudTextBottomPadding() { return 0; }
    private static int getHudOutlineThickness() { return getScaledHudPixels(1.0f); }

    private static int getHudRowHeight(int textTopOffset, int textBottomOffset, int textTopPadding, int textBottomPadding) {
        int textBoxHeight = Math.max(1, textBottomOffset - textTopOffset);
        return Math.max(1, textBoxHeight + textTopPadding + textBottomPadding);
    }

    private static float getHudTextY(float rowTop, int textTopOffset, int textTopPadding) {
        return rowTop + textTopPadding - textTopOffset;
    }

    private static int getScaledHudPixels(float basePixels) {
        return Math.max(1, Math.round(basePixels * getSelectedFontScale()));
    }

    // ========================================
    // Edit Screen - Drag to reposition HUD
    // ========================================

    static class EditScreen extends Screen {
        private static final String EXAMPLE = "This is an-Example-HUD";
        private boolean dragging = false;
        private float actualX, actualY;
        private int lastMouseX, lastMouseY;
        private float lastActualX, lastActualY;
        private float minX, minY, maxX, maxY, clickMinX;

        public EditScreen() {
            super(Text.literal("Edit HUD"));
        }

        @Override
        protected void init() {
            syncPositionToResolution();
            actualX = posX;
            actualY = posY;
            addDrawableChild(new net.minecraft.client.gui.widget.ButtonWidget(
                width - 100, height - 30, 90, 20, Text.literal("Reset"), button -> {
                    resetPosition();
                    actualX = posX;
                    actualY = posY;
                }));
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            ctx.fill(0, 0, width, height, 0xB0000000);

                syncPositionToResolution();
                actualX = posX;
                actualY = posY;
            }

            float previewX = actualX;
            float previewY = actualY;

            float[] clickPos = getPreviewBounds(EXAMPLE);
            minX = previewX;
            minY = previewY;

            if (clickPos == null) {
                maxX = previewX + 50.0f;
                maxY = previewY + 32.0f;
                clickMinX = previewX;
            } else {
                maxX = clickPos[0];
                maxY = clickPos[1];
                clickMinX = clickPos[2];
            }

            setAbsolutePosition(previewX, previewY);
            drawPreview(ctx);
            ctx.drawTextWithShadow(textRenderer, "Drag to reposition HUD", width / 2 - 60, height / 2 - 40, 0xFFFFFF);
            super.render(ctx, mouseX, mouseY, delta);
        }

        private float[] getPreviewBounds(String text) {
            RavenFontRenderer hudFont = getHudFontRenderer();
            if (empty()) {
                float x = minX;
                float y = minY;
                String[] lines = text.split("-");
                int localTextTopPadding = getHudTextTopPadding();
                int localTextBottomPadding = getHudTextBottomPadding();
                int localRowHeight = getHudRowHeight(hudFont.getTextTopOffset(), hudFont.getTextBottomOffset(), localTextTopPadding, localTextBottomPadding);
                for (String line : lines) {
                    if (alignRight.isToggled()) {
                        x += hudFont.getStringWidth(lines[0]) - hudFont.getStringWidth(line);
                    }
                    y += localRowHeight;
                }
                return null;
            }
            int longestModule = getLongestModule();
            return new float[]{minX + longestModule, maxY, minX - longestModule};
        }

        private boolean empty() {
            for (Module module : ModuleManager.getModules()) {
                    return false;
                }
            }
            return true;
        }

        private void drawPreview(DrawContext ctx) {
            RavenFontRenderer hudFont = getHudFontRenderer();
            float scale = getSelectedFontScale();
            int textTopOffset = hudFont.getTextTopOffset();
            int textTopPadding = getHudTextTopPadding();
            int rowHeight = getHudRowHeight(textTopOffset, hudFont.getTextBottomOffset(), textTopPadding, 0);

            String[] lines = EXAMPLE.split("-");
            float y = actualY;

            ctx.getMatrices().push();
            ctx.getMatrices().scale(scale, scale, 1.0f);

            for (String line : lines) {
                float textY = y + textTopPadding - textTopOffset;
                int color = 0xFFFFFFFF;
                if (shouldDrawTextShadow()) {
                    ctx.drawTextWithShadow(textRenderer, line, (int) (actualX / scale), (int) (textY / scale), color);
                } else {
                    ctx.drawText(textRenderer, line, (int) (actualX / scale), (int) (textY / scale), color);
                }
                y += rowHeight;
            }
            ctx.getMatrices().pop();
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (button == 0) {
                    if (mouseX > clickMinX \&\& mouseX < maxX \&\& mouseY > minY \&\& mouseY < maxY) {
                        dragging = true;
                        lastMouseX = (int) mouseX;
                        lastMouseY = (int) mouseY;
                        lastActualX = actualX;
                        lastActualY = actualY;
                    }
                }
                if (dragging) {
                    actualX = lastActualX + (float) (mouseX - lastMouseX);
                    actualY = lastActualY + (float) (mouseY - lastMouseY);
                    setAbsolutePosition(actualX, actualY);
                }
            }
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == 0 && dragging) {
                setAbsolutePosition(actualX, actualY);
                dragging = false;
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}
