package keystrokesmod.module.impl.render;

import keystrokesmod.event.Subscribe;
import keystrokesmod.event.RenderEvent;
import keystrokesmod.module.core.Module;
import keystrokesmod.module.setting.impl.BooleanSetting;
import keystrokesmod.module.setting.impl.NumberSetting;

public class ESP extends Module {
    private BooleanSetting players, mobs;
    private NumberSetting range;
    
    public ESP() {
        super("ESP", Category.RENDER);
    }
    
    @Override
    protected void initSettings() {
        registerSetting(players = new BooleanSetting("Players", true));
        registerSetting(mobs = new BooleanSetting("Mobs", true));
        registerSetting(range = new NumberSetting("Range", 64.0, 16.0, 256.0, 16.0));
    }
    
    @Override
    protected void onEnable() { System.out.println("[ESP] Enabled"); }
    @Override
    protected void onDisable() { System.out.println("[ESP] Disabled"); }
    
    @Subscribe
    public void onRender(RenderEvent event) {
        // TODO: Implement ESP rendering with DrawContext
    }
}