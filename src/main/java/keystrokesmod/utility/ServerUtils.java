package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerUtils implements IMinecraftInstance {
    private static int grimTransactionCount = 0;
    private static final Map<String, Integer> scoreboardHealth = new ConcurrentHashMap<>();
    private static float lastHealth = 20f;
    private static int lastFood = 20;
    private static float lastSaturation = 5f;
    
    public static void onScoreboardScore(ScoreboardScoreUpdateS2CPacket packet) {
        String objectiveName = packet.objectiveName();
        if (isHealthObjective(objectiveName)) {
            scoreboardHealth.put(packet.scoreHolderName(), packet.score());
        }
    }
    
    public static void onHealthUpdate(float health, int food, float saturation) {
        lastHealth = health;
        lastFood = food;
        lastSaturation = saturation;
    }
    
    public static float getHealth(LivingEntity entity) {
        if (entity == null) return 0f;
        String name = entity.getName().getString();
        Integer scoreHealth = scoreboardHealth.get(name);
        if (scoreHealth != null && scoreHealth > 0) {
            return scoreHealth;
        }
        return entity.getHealth() + entity.getAbsorptionAmount();
    }
    
    public static float getPlayerHealth(String playerName) {
        Integer health = scoreboardHealth.get(playerName);
        return health != null ? health : -1;
    }
    
    public static void setPlayerHealth(String playerName, int health) {
        scoreboardHealth.put(playerName, health);
    }
    
    private static boolean isHealthObjective(String objectiveName) {
        return "belowHealth".equalsIgnoreCase(objectiveName) || "health".equalsIgnoreCase(objectiveName);
    }
    
    public static Map<String, Integer> getAllScoreboardHealth() {
        return scoreboardHealth;
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
    
    public static void clearHealthData() {
        scoreboardHealth.clear();
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
