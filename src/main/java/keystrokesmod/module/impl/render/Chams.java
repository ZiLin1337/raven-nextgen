package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.awt.Color;

public class Chams extends Module {
    private static final MinecraftClient mc = mc;
    private SliderSetting mode;
    private ColorSetting color;
    private ButtonSetting invisible;
    private ButtonSetting playersOnly;
    private String[] modes = {"Normal", "Colored", "Textured"};

    public Chams() {
        super("Chams", category.render);
        registerSetting(mode = new SliderSetting("Mode", 0, modes));
        registerSetting(color = new ColorSetting("Color", 255, 85, 255));
        registerSetting(invisible = new ButtonSetting("Invisible players", true));
        registerSetting(playersOnly = new ButtonSetting("Players only", false));
    }

    public static boolean shouldRender(PlayerEntity player) {
        return player != null && player.isAlive() && !player.isRemoved();
    }

    public static int getColor() {
        return new Color(255, 85, 255, 100).getRGB();
    }
}
