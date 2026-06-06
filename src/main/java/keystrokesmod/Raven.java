package keystrokesmod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.command.CommandManager;
import keystrokesmod.event.KeyPressEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.ModuleManager;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;

public class Raven implements ClientModInitializer {
    public static boolean DEBUG = false;
    public static String clientName = "Raven bS";
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static ModuleManager moduleManager;
    public static ClickGui clickGui;
    // TODO: Create CommandManager
    public static Object commandManager;
    public static final IEventBus EVENT_BUS = new EventBus();
    
    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();
        clickGui = new ClickGui();
        commandManager = new CommandManager();
        EVENT_BUS.subscribe(this);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }
    
    private void onTick(MinecraftClient client) {
        if (mc.player == null || mc.world == null) return;
        for (Module m : moduleManager.getModules()) {
            if (m.isEnabled()) m.onUpdate();
        }
    }

    @EventHandler
    public void onSendPacket(SendPacketEvent e) {
        if (e.getPacket() instanceof ChatMessageC2SPacket chat) {
            if (commandManager.execute(chat.chatMessage())) e.setCanceled(true);
        }
    }

    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        if (mc.currentScreen != null) return;
        for (Module m : moduleManager.getModules()) {
            if (m.getKeycode() == e.getKeyCode()) m.toggle();
        }
    }
    
    public static ModuleManager getModuleManager() { return moduleManager; }
}