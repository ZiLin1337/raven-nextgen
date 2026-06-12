package keystrokesmod.utility;

import keystrokesmod.Raven;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class NetworkUtils implements IMinecraftInstance {
    
    public static void sendPacket(Packet<?> packet) {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(packet);
        }
    }
    
    public static void sendPosition(double x, double y, double z, boolean onGround) {
        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround, false));
    }
    
    public static void sendRotation(float yaw, float pitch, boolean onGround) {
        sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, false));
    }
    
    public static void sendFull(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround, false));
    }
    
    public static Vec3d getPacketPos() {
        return mc.player != null ? mc.player.getPos() : Vec3d.ZERO;
    }
    
    public static void sendPositionAndRotation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, false));
    }
}
