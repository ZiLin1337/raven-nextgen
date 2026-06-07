package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ButtonSetting serverSide;
    private float prevYaw, prevPitch;

    public NoRotate() {
        super("NoRotate", category.player);
        this.registerSetting(serverSide = new ButtonSetting("Server side", false));
    }

    public void onReceivePacket(ReceivePacketEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
            // Store rotation values from server packet
            try {
                // Try standard mapped names first
                prevYaw = packet.getYaw();
                prevPitch = packet.getPitch();
            } catch (Exception e) {
                // Fallback: try via reflection
                try {
                    var clazz = packet.getClass();
                    var yawField = clazz.getDeclaredField("yaw");
                    yawField.setAccessible(true);
                    prevYaw = yawField.getFloat(packet);
                    
                    var pitchField = clazz.getDeclaredField("pitch");
                    pitchField.setAccessible(true);
                    prevPitch = pitchField.getFloat(packet);
                } catch (Exception ex) {
                    // Silently fail
                }
            }
            
            if (serverSide.isToggled()) {
                // Packet will be modified by mixin to restore original rotation
            }
        }
    }
}
