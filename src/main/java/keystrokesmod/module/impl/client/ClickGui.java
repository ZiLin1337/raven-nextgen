package keystrokesmod.module.impl.client;

import keystrokesmod.module.core.Module;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", Category.CLIENT);
    }
    
    @Override
    protected void initSettings() {}
    
    @Override
    protected void onEnable() { System.out.println("[ClickGui] Opening..."); }
    @Override
    protected void onDisable() { System.out.println("[ClickGui] Closed"); }
}