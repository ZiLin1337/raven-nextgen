package keystrokesmod.utility;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

public class EntityUtils implements IMinecraftInstance {
    
    public static PlayerEntity getClosestPlayer(double range) {
        if (mc.world == null) return null;
        return mc.world.getPlayers().stream()
            .filter(e -> !(e instanceof ClientPlayerEntity) && !e.isSpectator())
            .filter(e -> mc.player.squaredDistanceTo(e) <= range * range)
            .min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)))
            .orElse(null);
    }
    
    public static boolean isInsideBlock() {
        if (mc.world == null || mc.player == null) return false;
        BlockPos pos = mc.player.getBlockPos();
        if (mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST) {
            return true;
        }
        return mc.world.canCollide(mc.player, mc.player.getBoundingBox());
    }
    
    public static boolean isInWeb(PlayerEntity player) {
        if (mc.world == null || player == null) return false;
        BlockPos pos = player.getBlockPos();
        return mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB ||
               mc.world.getBlockState(pos.up()).getBlock() == Blocks.COBWEB;
    }
    
    public static boolean isEntityLiving(Entity entity) {
        return entity instanceof net.minecraft.entity.LivingEntity;
    }
    
    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }
    
    public static boolean isSelf(Entity entity) {
        return entity == mc.player;
    }
    
    public static List<PlayerEntity> getPlayersInRange(double range) {
        if (mc.world == null || mc.player == null) return List.of();
        return mc.world.getPlayers().stream()
            .filter(e -> !(e instanceof ClientPlayerEntity))
            .filter(e -> mc.player.squaredDistanceTo(e) <= range * range)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public static boolean canBeSeen(Entity target, Entity from, double range) {
        if (mc.world == null) return false;
        return mc.world.raycast(new net.minecraft.world.RaycastContext(from.getEyePos(), target.getPos(), net.minecraft.world.RaycastContext.ShapeType.COLLIDER, net.minecraft.world.RaycastContext.FluidHandling.NONE, mc.player)).getType() == net.minecraft.util.hit.HitResult.Type.MISS;
    }
}
