package keystrokesmod.utility.color;

import java.awt.Color;

public class ColorConstants {
    public static final int WHITE = Color.WHITE.getRGB();
    public static final int BLACK = Color.BLACK.getRGB();
    public static final int RED = Color.RED.getRGB();
    public static final int GREEN = Color.GREEN.getRGB();
    public static final int BLUE = Color.BLUE.getRGB();
    public static final int YELLOW = Color.YELLOW.getRGB();
    public static final int CYAN = Color.CYAN.getRGB();
    public static final int MAGENTA = Color.MAGENTA.getRGB();
    public static final int ORANGE = Color.ORANGE.getRGB();
    public static final int PINK = Color.PINK.getRGB();
    public static final int GRAY = Color.GRAY.getRGB();
    public static final int DARK_GRAY = Color.DARK_GRAY.getRGB();
    
    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
    
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    public static int lerp(int start, int end, float t) {
        int a1 = (start >> 24) & 0xFF, r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int a2 = (end >> 24) & 0xFF, r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
