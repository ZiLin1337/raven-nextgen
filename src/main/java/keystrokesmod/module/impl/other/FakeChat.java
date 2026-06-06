package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;

public class FakeChat extends Module {
    private static final MinecraftClient mc = mc;
    private SliderSetting mode;
    private String[] modes = {"Prefix", "Suffix", "Replace"};
    private String fakeMessage;

    public FakeChat() {
        super("Fake Chat", category.other);
        registerSetting(mode = new SliderSetting("Mode", 0, modes));
    }

    public String processChat(String message) {
        if (!isEnabled()) return message;
        int m = (int) mode.getInput();
        if (m == 0) return ">>> " + message;
        if (m == 1) return message + " <<<";
        return "[CLIENT] " + message;
    }

    public boolean shouldCancel(String message) {
        return isEnabled() && fakeMessage != null && message.equals(fakeMessage);
    }
}
