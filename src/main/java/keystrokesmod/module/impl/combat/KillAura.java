package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import java.util.*;

/**
 * KillAura - simplified for 1.21.4 Fabric
 * TODO: Re-implement full targeting and rotation logic
 */
public class KillAura extends Module {
    private final SliderSetting targetCPS;
    private final SliderSetting fov;
    private final SliderSetting attackRange;
    private final SliderSetting aimRange;
    private final ButtonSetting attackMobs;
    private final ButtonSetting targetInvis;
    
    private LivingEntity currentTarget;
    private long lastAttackTime = 0;
    
    public KillAura() {
        super("KillAura", Module.category.combat);
        this.targetCPS = new SliderSetting("CPS", 10, 1, 20, 1);
        this.fov = new SliderSetting("FOV", 360, 30, 360, 1);
        this.attackRange = new SliderSetting("Attack range", 3.5, 1, 6, 0.1);
        this.aimRange = new SliderSetting("Aim range", 4.5, 1, 6, 0.1);
        this.attackMobs = new ButtonSetting("Attack mobs", false);
        this.targetInvis = new ButtonSetting("Target invisibles", false);
    }
    
    @Override
    public void onUpdate() {
        if (!this.isEnabled()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        // Find target
        LivingEntity target = findTarget(mc);
        if (target == null) {
            currentTarget = null;
            return;
        }
        
        currentTarget = target;
        
        // Attack if CPS allows
        long now = System.currentTimeMillis();
        long delay = (long)(1000.0 / targetCPS.getInput());
        if (now - lastAttackTime >= delay) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            lastAttackTime = now;
        }
    }
    
    private LivingEntity findTarget(MinecraftClient mc) {
        LivingEntity best = null;
        double bestDist = attackRange.getInput() + 1;
        
        for (net.minecraft.entity.Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) entity;
            
            if (living == mc.player) continue;
            if (living.isRemoved()) continue;
            if (living.getHealth() <= 0) continue;
            if (!targetInvis.isToggled() && living.isInvisible()) continue;
            if (living instanceof PlayerEntity) {
                // Skip players for now - TODO: check teams
                continue;
            }
            if (!attackMobs.isToggled()) continue;
            
            double dist = mc.player.distanceTo(living);
            if (dist < bestDist) {
                best = living;
                bestDist = dist;
            }
        }
        
        return best;
    }
    
    @Override
    public void onDisable() {
        currentTarget = null;
    }
}
