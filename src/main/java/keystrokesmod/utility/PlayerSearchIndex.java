package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerSearchIndex implements IMinecraftInstance {
    
    public static PlayerEntity getClosestPlayer(double range) {
        if (mc.world == null || mc.player == null) return null;
        PlayerEntity closest = null;
        double closestDist = range;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            double dist = player.squaredDistanceTo(mc.player);
            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }
        return closest;
    }
    
    public static PlayerEntity getClosestEnemy(double range) {
        if (mc.world == null || mc.player == null) return null;
        PlayerEntity closest = null;
        double closestDist = range;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (Utils.enemies.contains(player.getName().getString().toLowerCase())) {
                double dist = player.squaredDistanceTo(mc.player);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = player;
                }
            }
        }
        return closest;
    }
    
    public static List<PlayerEntity> getPlayersInRange(double range) {
        List<PlayerEntity> players = new ArrayList<>();
        if (mc.world == null || mc.player == null) return players;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.squaredDistanceTo(mc.player) <= range * range) {
                players.add(player);
            }
        }
        return players;
    }
    
    public static boolean isBot(PlayerEntity player) {
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (entry == null) return true;
        return entry.getProfile().getName() == null;
    }
}
