package keystrokesmod.utility;

import java.awt.Color;

public class Theme {
    private static int mainColor = 0xFF00FF00;
    private static int backgroundColor = 0x80000000;
    private static int textColor = 0xFFFFFFFF;
    private static int accentColor = 0xFF00AAFF;
    private static int errorColor = 0xFFFF0000;
    private static int warningColor = 0xFFFFFF00;
    private static int successColor = 0xFF00FF00;
    
    public static int getMainColor() { return mainColor; }
    public static void setMainColor(int color) { mainColor = color; }
    
    public static int getBackgroundColor() { return backgroundColor; }
    public static void setBackgroundColor(int color) { backgroundColor = color; }
    
    public static int getTextColor() { return textColor; }
    public static void setTextColor(int color) { textColor = color; }
    
    public static int getAccentColor() { return accentColor; }
    public static void setAccentColor(int color) { accentColor = color; }
    
    public static int getErrorColor() { return errorColor; }
    public static int getWarningColor() { return warningColor; }
    public static int getSuccessColor() { return successColor; }
    
    public static int getRainbowColor(long offset, float saturation) {
        float hue = (float) ((System.currentTimeMillis() + offset) % 10000L) / 10000F;
        return Color.HSBtoRGB(hue, saturation, 1.0F);
    }
    
    public static int getHealthColor(float health, float maxHealth) {
        float ratio = health / maxHealth;
        int r = (int) (255 * (1 - ratio));
        int g = (int) (255 * ratio);
        return 0xFF000000 | (r << 16) | (g << 8);
    }
}
