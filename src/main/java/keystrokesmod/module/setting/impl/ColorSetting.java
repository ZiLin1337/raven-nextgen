package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class ColorSetting extends Setting {
    public GroupSetting groupSetting;
    private int red;
    private int green;
    private int blue;
    private int alpha = 255;

    public ColorSetting(String name, int red, int green, int blue) {
        super(name);
        setColor(red, green, blue);
    }

    public ColorSetting(String name, int red, int green, int blue, int alpha) {
        super(name);
        setColor(red, green, blue, alpha);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public void setColor(int red, int green, int blue) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
    }

    public void setColor(int red, int green, int blue, int alpha) {
        setColor(red, green, blue);
        this.alpha = clamp(alpha);
    }

    public int getColor() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int getRGB() {
        return getColor();
    }

    public boolean hasAlpha() {
        return alpha < 255;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = clamp(alpha);
    }

    public float getHue() {
        return 0.0F;
    }

    public float getSaturation() {
        return 0.0F;
    }

    public float getBrightness() {
        return 0.0F;
    }

    public void setFromHSB(float hue, float saturation, float brightness) {
    }

    @Override
    public void loadProfile(JsonObject data) {
    }
}