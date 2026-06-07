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
            try {
                // Try reflection to access yaw/pitch fields
                var clazz = packet.getClass();
                try {
                    var yawField = clazz.getDeclaredField("yaw");
                    yawField.setAccessible(true);
                    prevYaw = yawField.getFloat(packet);
                    
                    var pitchField = clazz.getDeclaredField("pitch");
                    pitchField.setAccessible(true);
                    prevPitch = pitchField.getFloat(packet);
                } catch (NoSuchFieldException e) {
                    // Try mapped names
                    try {
                        var yawField = clazz.getDeclaredField("field_149475_f");
                        yawField.setAccessible(true);
                        prevYaw = yawField.getFloat(packet);
                        
                        var pitchField = clazz.getDeclaredField("field_149477_g");
                        pitchField.setAccessible(true);
                        prevPitch = pitchField.getFloat(packet);
                    } catch (NoSuchFieldException ex) {
                        // Last resort
                    }
                }
            } catch (Exception e) {
                // Silently fail
            }
        }
    }
}
