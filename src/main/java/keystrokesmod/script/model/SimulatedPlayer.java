package keystrokesmod.script.model;

import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3dd;

public class SimulatedPlayer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;
    public boolean isSneaking;
    public boolean isSprinting;

    public SimulatedPlayer() {
        if (mc.player == null) return;
        this.x = mc.player.getX();
        this.y = mc.player.getY();
        this.z = mc.player.getZ();
        this.yaw = mc.player.getYaw();
        this.pitch = mc.player.getPitch();
        this.onGround = mc.player.isOnGround();
        this.isSneaking = mc.player.isSneaking();
        this.isSprinting = mc.player.isSprinting();
    }

    public SimulatedPlayer(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public void sendPacket() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround));
    }

    public void sendPosition() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }

    public void sendRotation() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround));
    }

    public void teleportTo(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        sendPacket();
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vec3dd getPositionVec() {
        return new Vec3dd(x, y, z);
    }

    public double distanceTo(SimulatedPlayer other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    public double distanceToXZ(SimulatedPlayer other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(z - other.z, 2));
    }
}
