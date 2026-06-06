package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import net.minecraft.client.MinecraftClient;

public class FastMine extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private SliderSetting speed;
    private SliderSetting mode;
    private ButtonSetting doubleBreak;
    private ButtonSetting instantBreak;
    private String[] modes = {"Speed", "Double", "Instant"};

    public FastMine() {
        super("Fast Mine", category.player);
        registerSetting(speed = new SliderSetting("Speed", 1.5, 1.0, 5.0, 0.1));
        registerSetting(mode = new SliderSetting("Mode", 0, modes));
        registerSetting(doubleBreak = new ButtonSetting("Double break", false));
        registerSetting(instantBreak = new ButtonSetting("Instant break", false));
    }

    public float getMineSpeed() {
        return isEnabled() ? (float) speed.getInput() : 1.0f;
    }

    public boolean shouldDoubleBreak() {
        return isEnabled() && doubleBreak.isToggled();
    }

    public boolean shouldInstantBreak() {
        return isEnabled() && instantBreak.isToggled();
    }
}
