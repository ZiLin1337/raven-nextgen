package keystrokesmod.utility.color;

public class ColorConstants {
    public static final int GUI_BG = 0x80000000;
    public static final int GUI_TITLE = 0xFF4444FF;
    public static final int MODULE_ENABLED = 0x8000AA00;
    public static final int MODULE_DISABLED = 0x80333333;
    public static final int TEXT_WHITE = 0xFFFFFFFF;
    public static final int TEXT_GRAY = 0xFFAAAAAA;
    public static final int TEXT_GREEN = 0xFF00FF00;
    public static final int TEXT_RED = 0xFFFF0000;
    public static final int SLIDER_BG = 0x80222222;
    public static final int SLIDER_FILL = 0xFF00AAFF;
    public static final int BUTTON_ON = 0xFF00AA00;
    public static final int BUTTON_OFF = 0xFF444444;

    public static int getHealthColor(float health, float maxHealth) {
        float percent = health / maxHealth;
        if (percent > 0.5f) return 0xFF00FF00;
        if (percent > 0.25f) return 0xFFFFFF00;
        return 0xFFFF0000;
    }
}
