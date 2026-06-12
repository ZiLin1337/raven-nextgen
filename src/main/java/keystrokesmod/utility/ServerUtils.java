package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerUtils implements IMinecraftInstance {
    private static int grimTransactionCount = 0;
    public static final Map<String, AtomicInteger> playerHealths = new HashMap<>();
    private static float lastHealth = 20f;
    private static int lastFood = 20;
    private static float lastSaturation = 5f;
    
    public static void onScoreboardScore(ScoreboardScoreUpdateS2CPacket packet) {
        if (mc.player == null || mc.world == null) return;
        String objectiveName = packet.objectiveName();
        String playerName = packet.scoreHolderName();
        int score = packet.score();
        if (!playerName.equals(mc.player.getName().getString())) {
            if ("belowHealth".equals(objectiveName) || "health".equals(objectiveName)) {
                if (!playerHealths.containsKey(playerName)) {
                    playerHealths.put(playerName, new AtomicInteger());
                }
                playerHealths.get(playerName).set(score);
            }
        }
    }
    
    public static void onHealthUpdate(float health, int food, float saturation) {
        lastHealth = health;
        lastFood = food;
        lastSaturation = saturation;
    }
    
    public static float getPlayerHealth(String playerName) {
        AtomicInteger health = playerHealths.get(playerName);
        return health != null ? health.get() : -1;
    }
    
    public static void setPlayerHealth(String playerName, float health) {
        if (!playerHealths.containsKey(playerName)) {
            playerHealths.put(playerName, new AtomicInteger());
        }
        playerHealths.get(playerName).set((int) health);
    }
    
    public static Map<String, AtomicInteger> getAllPlayerHealths() {
        return playerHealths;
    }
    
    public static float getLastHealth() {
        return lastHealth;
    }
    
    public static int getLastFood() {
        return lastFood;
    }
    
    public static float getLastSaturation() {
        return lastSaturation;
    }
    
    public static void updatePlayerHealths() {
        if (mc.world == null) return;
        for (var player : mc.world.getPlayers()) {
            if (player != mc.player && playerHealths.containsKey(player.getName().getString())) {
                int health = playerHealths.get(player.getName().getString()).get();
                player.setHealth(Math.max(1, health));
            }
        }
    }
    
    public static void clearHealthData() {
        playerHealths.clear();
        grimTransactionCount = 0;
    }
    
    public static void incrementGrimTransaction() {
        grimTransactionCount++;
    }
    
    public static int getGrimTransactionCount() {
        return grimTransactionCount;
    }
    
    public static void resetGrimTransaction() {
        grimTransactionCount = 0;
    }
}
