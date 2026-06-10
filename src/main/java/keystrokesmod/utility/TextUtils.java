package keystrokesmod.utility;

import net.minecraft.text.Text;

public class TextUtils {
    public static String stripFormatting(Text text) {
        return text != null ? text.getString() : "";
    }

    public static String getFormattedText(Text text) {
        return text != null ? text.getString() : "";
    }

    public static String getUnformattedText(Text text) {
        return text != null ? text.getString() : "";
    }

    public static boolean isEmpty(Text text) {
        return text == null || text.getString().isEmpty();
    }
}