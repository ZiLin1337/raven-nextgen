package keystrokesmod.module.impl.movement;

import keystrokesmod.event.Subscribe;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.core.Module;
import keystrokesmod.module.setting.impl.ModeSetting;

public class Sprint extends Module {
    private ModeSetting mode;
    
    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }
    
    @Override
    protected void initSettings() {
        registerSetting(mode = new ModeSetting("Mode", new String[]{"Legit", "Omni"}, 0));
    }
    
    @Override
    protected void onEnable() { System.out.println("[Sprint] Enabled"); }
    @Override
    protected void onDisable() { System.out.println("[Sprint] Disabled"); }
    
    @Subscribe
    public void onTick(TickEvent event) {
        // TODO: Implement sprint logic
    }
}