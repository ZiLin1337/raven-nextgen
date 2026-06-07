package keystrokesmod.module.setting.impl;
import keystrokesmod.module.setting.Setting;
import java.awt.Color;
public class ColorSetting extends Setting {
	protected int color = 0xFF00FF;
	protected boolean rainbow = false;
    private int red, green, blue, alpha;
    private final boolean hasAlpha;
    public GroupSetting groupSetting;
    public ColorSetting(String name, int red, int green, int blue) {
        this(null, name, red, green, blue, 255, false);
    }
    public ColorSetting(String name, int red, int green, int blue, int alpha) {
        this(null, name, red, green, blue, alpha, true);
    }
    public ColorSetting(GroupSetting gs, String name, int red, int green, int blue) {
        this(gs, name, red, green, blue, 255, false);
    }
    public ColorSetting(GroupSetting gs, String name, int red, int green, int blue, int alpha) {
        this(gs, name, red, green, blue, alpha, true);
    }
    public ColorSetting(GroupSetting gs, String name, int red, int green, int blue, int alpha, boolean hasAlpha) {
        super(name);
        this.groupSetting = gs;
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
        this.hasAlpha = hasAlpha;
    }
    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getAlpha() { return alpha; }
    public void setAlpha(int a) { this.alpha = clamp(a); }
    public void setColor(int r, int g, int b) { this.red=clamp(r); this.green=clamp(g); this.blue=clamp(b); }
    public void setColor(int r, int g, int b, int a) { setColor(r,g,b); this.alpha=clamp(a); }
    public int getColor() { return (alpha<<24)|(red<<16)|(green<<8)|blue; }
    public int getRGB() { return (red<<16)|(green<<8)|blue; }
    public boolean hasAlpha() { return hasAlpha; }
    public float getHue() { float[] hsb=Color.RGBtoHSB(red,green,blue,null); return hsb[0]*360f; }
    public float getSaturation() { return Color.RGBtoHSB(red,green,blue,null)[1]; }
    public float getBrightness() { return Color.RGBtoHSB(red,green,blue,null)[2]; }
    public void setFromHSB(float h, float s, float b) {
        int rgb = Color.HSBtoRGB(h/360f, Math.max(0f,Math.min(1f,s)), Math.max(0f,Math.min(1f,b)));
        this.red=(rgb>>16)&0xFF; this.green=(rgb>>8)&0xFF; this.blue=rgb&0xFF;
    }
    public void setHue(float h) { float[] hsb=Color.RGBtoHSB(red,green,blue,null); setFromHSB(h,hsb[1],hsb[2]); }
    public void setSaturation(float s) { float[] hsb=Color.RGBtoHSB(red,green,blue,null); setFromHSB(hsb[0]*360f,s,hsb[2]); }
    public void setBrightness(float b) { float[] hsb=Color.RGBtoHSB(red,green,blue,null); setFromHSB(hsb[0]*360f,hsb[1],b); }
    private static int clamp(int v) { return Math.max(0,Math.min(255,v)); }
    @Override
    public void loadProfile(com.google.gson.JsonObject json) {
        if (json.has("color")) this.color = json.get("color").getAsInt();
        if (json.has("rainbow")) this.rainbow = json.get("rainbow").getAsBoolean();
    }
}