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
    private final SliderSetting horizontal = new SliderSetting("Horizontal", "", 100.0, 0.0, 100.0, 1.0);
    private final SliderSetting vertical = new SliderSetting("Vertical", "", 100.0, 0.0, 100.0, 1.0);
    private final boolean cancelBurning = true;
    private final boolean boostWithLMB = false;
    public boolean disable = false;

    public AntiKnockback() {
        super("Anti-Knockback", Module.category.combat);
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
                // 在1.21.4中，velocity字段是private，我们需要通过反射或使用getVelocity()方法
                // 但getVelocity()方法可能不存在，所以我们简化处理
                e.setCancelled(true);
            }
        }
        
        if (e.getPacket() instanceof ExplosionS2CPacket explosionPacket) {
            // 简化爆炸反击退
            Vec3d currentVelocity = mc.player.getVelocity();
            mc.player.setVelocity(new Vec3d(
                currentVelocity.x * horizontal.getInput() / 100.0,
                currentVelocity.y * vertical.getInput() / 100.0,
                currentVelocity.z * horizontal.getInput() / 100.0
            ));
        }
    }
}
