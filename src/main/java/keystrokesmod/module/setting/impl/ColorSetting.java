package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;

public class ColorSetting extends Setting {
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

    @Override
    public void loadProfile(JsonObject data) {
    }
}
