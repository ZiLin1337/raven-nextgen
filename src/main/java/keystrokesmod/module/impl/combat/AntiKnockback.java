package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class AntiKnockback extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final SliderSetting horizontal = new SliderSetting("Horizontal", 100, 0, 100, 1, "%");
    private final SliderSetting vertical = new SliderSetting("Vertical", 100, 0, 100, 1, "%");
    private final boolean cancelBurning = true;
    private final boolean boostWithLMB = false;
    private boolean disable = false;

    public AntiKnockback() {
        super("Anti-Knockback");
        this.registerSetting(horizontal);
        this.registerSetting(vertical);
    }

    @Override
    public void onEnable() {
        this.disable = false;
    }

    @Override
    public void onDisable() {
        this.disable = false;
    }

    public void onReceivePacket(ReceivePacketEvent e) {
        if (mc.player == null) return;
        
        if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket velocityPacket) {
            if (velocityPacket.getEntityId() == mc.player.getId() && !disable) {
                Vec3d packetVelocity = velocityPacket.getVelocity();
                double newX = (packetVelocity.x / 8000.0) * horizontal.getInput() / 100.0;
                double newY = (packetVelocity.y / 8000.0) * vertical.getInput() / 100.0;
                double newZ = (packetVelocity.z / 8000.0) * horizontal.getInput() / 100.0;
                
                mc.player.setVelocity(new Vec3d(newX, newY, newZ));
                e.setCancelled(true);
            }
        }
        
        if (e.getPacket() instanceof ExplosionS2CPacket explosionPacket) {
            Vec3d packetVelocity = explosionPacket.getVelocity();
            double addX = packetVelocity.x * horizontal.getInput() / 100.0;
            double addY = packetVelocity.y * vertical.getInput() / 100.0;
            double addZ = packetVelocity.z * horizontal.getInput() / 100.0;
            
            Vec3d currentVelocity = mc.player.getVelocity();
            mc.player.setVelocity(new Vec3d(
                currentVelocity.x + addX,
                currentVelocity.y + addY,
                currentVelocity.z + addZ
            ));
        }
    }
}
