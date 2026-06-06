package keystrokesmod.module.impl.other;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import java.util.Random;

public class ChatBypass extends Module {
    private static final MinecraftClient mc = mc;
    private SliderSetting mode;
    private SliderSetting spacing;
    private ButtonSetting unicode;
    private ButtonSetting zeroWidth;
    private String[] modes = {"Normal", "Fancy", "Glitch", "Invisible"};
    private final Random random = new Random();

    public ChatBypass() {
        super("Chat Bypass", category.other);
        registerSetting(mode = new SliderSetting("Mode", 0, modes));
        registerSetting(spacing = new SliderSetting("Spacing", 0, 0, 5, 1));
        registerSetting(unicode = new ButtonSetting("Unicode", false));
        registerSetting(zeroWidth = new ButtonSetting("Zero width", false));
    }

    public String bypass(String message) {
        if (!isEnabled()) return message;
        switch ((int) mode.getInput()) {
            case 0: return applyNormal(message);
            case 1: return applyFancy(message);
            case 2: return applyGlitch(message);
            case 3: return applyInvisible(message);
        }
        return message;
    }

    private String applyNormal(String msg) {
        StringBuilder sb = new StringBuilder();
        int space = (int) spacing.getInput();
        for (int i = 0; i < msg.length(); i++) {
            sb.append(msg.charAt(i));
            if (space > 0 && i < msg.length() - 1) {
                for (int j = 0; j < space; j++) sb.append(' ');
            }
        }
        return sb.toString();
    }

    private String applyFancy(String msg) {
        StringBuilder sb = new StringBuilder();
        for (char c : msg.toCharArray()) {
            if (c >= 'a' && c <= 'z') sb.append((char) (c - 32));
            else if (c >= 'A' && c <= 'Z') sb.append((char) (c + 32));
            else sb.append(c);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    private String applyGlitch(String msg) {
        StringBuilder sb = new StringBuilder();
        String glitchChars = "\u0300\u0301\u0302\u0303\u0304\u0305\u0306\u0307\u0308\u0309\u030A\u030B";
        for (char c : msg.toCharArray()) {
            sb.append(c);
            if (random.nextBoolean()) sb.append(glitchChars.charAt(random.nextInt(glitchChars.length())));
        }
        return sb.toString();
    }

    private String applyInvisible(String msg) {
        StringBuilder sb = new StringBuilder();
        String invisible = "\u200B\u200C\u200D\uFEFF";
        for (char c : msg.toCharArray()) {
            sb.append(c);
            if (random.nextInt(3) == 0) sb.append(invisible.charAt(random.nextInt(invisible.length())));
        }
        return sb.toString();
    }
}
