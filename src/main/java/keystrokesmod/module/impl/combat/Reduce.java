package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
// 
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import meteordevelopment.orbit.EventHandler;

public class Reduce extends Module {
    public SliderSetting horizontal, vertical;

    public Reduce() {
        super("Reduce", category.combat);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 50, 0, 100, 1));
        this.registerSetting(vertical = new SliderSetting("Vertical", 50, 0, 100, 1));
    }

    @EventHandler
    public void onKnockback(PreKnockbackEvent e) {
        double h = (100.0 - horizontal.getInput()) / 100.0;
        double v = (100.0 - vertical.getInput()) / 100.0;
        e.setHorizontal(e.getHorizontal() * h);
        e.setVertical(e.getVertical() * v);
    }
}
