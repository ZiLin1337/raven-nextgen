package keystrokesmod;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.event.KeyPressEvent;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;

public class Raven implements ClientModInitializer {
    public static boolean DEBUG = false;
    public static String clientName = "Raven bS";
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static ModuleManager moduleManager;
    public static ClickGui clickGui;
    public static keystrokesmod.command.CommandManager commandManager;
    public static keystrokesmod.utility.profile.ProfileManager profileManager;
    public static keystrokesmod.utility.profile.Profile currentProfile;
    public static keystrokesmod.script.ScriptManager scriptManager;
    public static final IEventBus EVENT_BUS = new EventBus();
    
    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();
        clickGui = new ClickGui();
        commandManager = new keystrokesmod.command.CommandManager();
        profileManager = new keystrokesmod.utility.profile.ProfileManager();
        scriptManager = new keystrokesmod.script.ScriptManager();
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
    public static IEventBus getEventBus() { return EVENT_BUS; }
}
