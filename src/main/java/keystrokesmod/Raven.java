mport keystrokesmod.event.KeyPressEvent;
package keystrokesmod;
mport keystrokesmod.event.KeyPressEvent;

mport keystrokesmod.event.KeyPressEvent;
import net.fabricmc.api.ClientModInitializer;
mport keystrokesmod.event.KeyPressEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
mport keystrokesmod.event.KeyPressEvent;
import net.minecraft.client.MinecraftClient;
mport keystrokesmod.event.KeyPressEvent;
import keystrokesmod.clickgui.ClickGui;
mport keystrokesmod.event.KeyPressEvent;
import keystrokesmod.module.ModuleManager;
mport keystrokesmod.event.KeyPressEvent;
import meteordevelopment.orbit.EventBus;
mport keystrokesmod.event.KeyPressEvent;
import meteordevelopment.orbit.IEventBus;
mport keystrokesmod.event.KeyPressEvent;

mport keystrokesmod.event.KeyPressEvent;
public class Raven implements ClientModInitializer {
mport keystrokesmod.event.KeyPressEvent;
    public static boolean DEBUG = false;
mport keystrokesmod.event.KeyPressEvent;
    public static String clientName = "Raven bS";
import keystrokesmod.event.SendPacketEvent;
mport keystrokesmod.event.KeyPressEvent;
    public static MinecraftClient mc = MinecraftClient.getInstance();
mport keystrokesmod.event.KeyPressEvent;
    public static ModuleManager moduleManager;
mport keystrokesmod.event.KeyPressEvent;
    public static ClickGui clickGui;
mport keystrokesmod.event.KeyPressEvent;
    public static CommandManager commandManager;
mport keystrokesmod.event.KeyPressEvent;
    public static final IEventBus EVENT_BUS = new EventBus();
mport keystrokesmod.event.KeyPressEvent;
    
mport keystrokesmod.event.KeyPressEvent;
    @Override
mport keystrokesmod.event.KeyPressEvent;
    public void onInitializeClient() {
mport keystrokesmod.event.KeyPressEvent;
        moduleManager = new ModuleManager();
mport keystrokesmod.event.KeyPressEvent;
        clickGui = new ClickGui();
mport keystrokesmod.event.KeyPressEvent;
        commandManager = new CommandManager();
mport keystrokesmod.event.KeyPressEvent;
        EVENT_BUS.subscribe(this);
mport keystrokesmod.event.KeyPressEvent;
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
mport keystrokesmod.event.KeyPressEvent;
    }
mport keystrokesmod.event.KeyPressEvent;
    
mport keystrokesmod.event.KeyPressEvent;
    private void onTick(MinecraftClient client) {
mport keystrokesmod.event.KeyPressEvent;
        if (mc.player == null || mc.world == null) return;
mport keystrokesmod.event.KeyPressEvent;
        for (Module m : moduleManager.getModules()) {
mport keystrokesmod.event.KeyPressEvent;
            if (m.isEnabled()) m.onUpdate();
mport keystrokesmod.event.KeyPressEvent;
        }
mport keystrokesmod.event.KeyPressEvent;
    }
mport keystrokesmod.event.KeyPressEvent;

mport keystrokesmod.event.KeyPressEvent;
    @EventHandler
mport keystrokesmod.event.KeyPressEvent;
    public void onSendPacket(SendPacketEvent e) {
mport keystrokesmod.event.KeyPressEvent;
        if (e.getPacket() instanceof ChatMessageC2SPacket chat) {
mport keystrokesmod.event.KeyPressEvent;
            if (commandManager.execute(chat.chatMessage())) e.setCanceled(true);
mport keystrokesmod.event.KeyPressEvent;
        }
mport keystrokesmod.event.KeyPressEvent;
    }
mport keystrokesmod.event.KeyPressEvent;

mport keystrokesmod.event.KeyPressEvent;
    @EventHandler
mport keystrokesmod.event.KeyPressEvent;
    public void onKeyPress(KeyPressEvent e) {
mport keystrokesmod.event.KeyPressEvent;
        if (mc.currentScreen != null) return;
mport keystrokesmod.event.KeyPressEvent;
        for (Module m : moduleManager.getModules()) {
mport keystrokesmod.event.KeyPressEvent;
            if (m.getKeycode() == e.getKeyCode()) m.toggle();
mport keystrokesmod.event.KeyPressEvent;
        }
mport keystrokesmod.event.KeyPressEvent;
    }
mport keystrokesmod.event.KeyPressEvent;
    
mport keystrokesmod.event.KeyPressEvent;
    public static ModuleManager getModuleManager() { return moduleManager; }
mport keystrokesmod.event.KeyPressEvent;
}