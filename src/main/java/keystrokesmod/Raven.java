package keystrokesmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import keystrokesmod.event.EventBus;
import keystrokesmod.module.core.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.impl.movement.Sprint;
import keystrokesmod.module.impl.render ESP;
import keystrokesmod.module.impl.render.Fullbright;
import keystrokesmod.module.impl.client.ClickGui;

/**
 * Raven NextGen - 1.21.4 Fabric Client
 * Based on raven-bS, rewritten for 1.21.4
 */
public class Raven implements ModInitializer {
    public static final String MOD_ID = "raven-nextgen";
    private static Raven INSTANCE;
    private final ModuleManager moduleManager = ModuleManager.getINSTANCE();
    
    @Override
    public void onInitialize() {
        INSTANCE = this;
        System.out.println("[Raven] Initializing for 1.21.4...");
        
        // Register modules
        registerModules();
        
        System.out.println("[Raven] Initialized with " + moduleManager.getModules().size() + " modules");
    }
    
    private void registerModules() {
        // Combat
        moduleManager.register(new KillAura());
        
        // Movement
        moduleManager.register(new Sprint());
        
        // Render
        moduleManager.register(new ESP());
        moduleManager.register(new Fullbright());
        
        // Client
        moduleManager.register(new ClickGui());
    }
    
    public static Raven getINSTANCE() { return INSTANCE; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public static MinecraftClient mc() { return MinecraftClient.getInstance(); }
}
