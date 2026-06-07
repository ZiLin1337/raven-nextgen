package keystrokesmod.module.impl.render;

import keystrokesmod.event.Subscribe;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.core.Module;
import keystrokesmod.module.setting.impl.ModeSetting;

public class Fullbright extends Module {
    private ModeSetting mode;
    
    public Fullbright() {
        super("Fullbright", Category.RENDER);
    }
    
    @Override
    protected void initSettings() {
        registerSetting(mode = new ModeSetting("Mode", new String[]{"Gamma", "Potion"}, 0));
    }
    
    @Override
    protected void onEnable() { System.out.println("[Fullbright] Enabled"); }
    @Override
    protected void onDisable() { System.out.println("[Fullbright] Disabled"); }
    
    @Subscribe
    public void onTick(TickEvent event) {
        // TODO: Implement fullbright
    }
}