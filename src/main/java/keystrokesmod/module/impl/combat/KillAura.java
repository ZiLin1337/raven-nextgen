package keystrokesmod.module.impl.combat;

import keystrokesmod.event.Subscribe;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.core.Module;
import keystrokesmod.module.setting.impl.BooleanSetting;
import keystrokesmod.module.setting.impl.NumberSetting;

public class KillAura extends Module {
    private BooleanSetting players, mobs, animals;
    private NumberSetting range, cps;
    
    public KillAura() {
        super("KillAura", Category.COMBAT);
    }
    
    @Override
    protected void initSettings() {
        registerSetting(players = new BooleanSetting("Players", true));
        registerSetting(mobs = new BooleanSetting("Mobs", true));
        registerSetting(animals = new BooleanSetting("Animals", false));
        registerSetting(range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1));
        registerSetting(cps = new NumberSetting("CPS", 10.0, 1.0, 20.0, 1.0));
    }
    
    @Override
    protected void onEnable() { System.out.println("[KillAura] Enabled"); }
    @Override
    protected void onDisable() { System.out.println("[KillAura] Disabled"); }
    
    @Subscribe
    public void onTick(TickEvent event) {
        // TODO: Implement kill aura logic
    }
}