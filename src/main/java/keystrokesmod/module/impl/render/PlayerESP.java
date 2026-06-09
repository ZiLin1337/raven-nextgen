package keystrokesmod.module.impl.render;

import java.awt.Color;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;

public class PlayerESP extends Module {
    public static boolean renderingOutlinePass = false;
    public final ButtonSetting showInvis = new ButtonSetting("Show Invis", false);
    public final ButtonSetting outline = new ButtonSetting("Outline", false);
    public final ButtonSetting renderSelf = new ButtonSetting("Render Self", false);
    public final ButtonSetting rainbow = new ButtonSetting("Rainbow", false);
    public final ButtonSetting redOnDamage = new ButtonSetting("Red On Damage", false);
    public final ColorSetting color = new ColorSetting("Color", 255, 255, 255);

    public PlayerESP() {
        super("PlayerESP", category.render);
        registerSetting(showInvis);
        registerSetting(outline);
        registerSetting(renderSelf);
        registerSetting(rainbow);
        registerSetting(redOnDamage);
        registerSetting(color);
    }
}