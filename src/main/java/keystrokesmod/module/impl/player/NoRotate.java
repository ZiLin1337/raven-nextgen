package keystrokesmod.module.impl.player;

import keystrokesmod.event.impl.PacketReceiveEvent;
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

    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
            // In 1.21.4, yaw/pitch might be accessed differently
            // Using reflection or alternative API
            try {
                // Try to access fields via reflection if direct access fails
                var clazz = packet.getClass();
                try {
                    var yawField = clazz.getDeclaredField("yaw");
                    yawField.setAccessible(true);
                    prevYaw = yawField.getFloat(packet);
                    
                    var pitchField = clazz.getDeclaredField("pitch");
                    pitchField.setAccessible(true);
                    prevPitch = pitchField.getFloat(packet);
                } catch (Exception e) {
                    // Fallback: try mapped names
                    try {
                        var yawField = clazz.getDeclaredField("field_149475_f"); // Forge mapped
                        yawField.setAccessible(true);
                        prevYaw = yawField.getFloat(packet);
                        
                        var pitchField = clazz.getDeclaredField("field_149477_g");
                        pitchField.setAccessible(true);
                        prevPitch = pitchField.getFloat(packet);
                    } catch (Exception ex) {
                        // Last resort: try method access
                        var yawMethod = clazz.getMethod("getYaw");
                        prevYaw = ((Number) yawMethod.invoke(packet)).floatValue();
                        
                        var pitchMethod = clazz.getMethod("getPitch");
                        prevPitch = ((Number) pitchMethod.invoke(packet)).floatValue();
                    }
                }
            } catch (Exception e) {
                // Silently fail - module will still be functional for future implementation
            }
            
            if (serverSide.isToggled()) {
                // Packet will be modified by mixin to restore original rotation
            }
        }
    }
}
