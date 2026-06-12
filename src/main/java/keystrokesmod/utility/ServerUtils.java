package keystrokesmod.utility;

import java.util.HashMap;
import java.util.Map;

public class ServerUtils implements IMinecraftInstance {
    private static float lastHealth = 20f;
    private static int lastFood = 20;
    private static float lastSaturation = 5f;
    private static final Map<String, Float> playerHealths = new HashMap<>();
    
    public static void onHealthUpdate(float health, int food, float saturation) {
        lastHealth = health;
        lastFood = food;
        lastSaturation = saturation;
    }
    
    public static float getPlayerHealth(String playerName) {
        Float health = playerHealths.get(playerName);
        return health != null ? health : -1;
    }
    
    public static void setPlayerHealth(String playerName, float health) {
        playerHealths.put(playerName, health);
    }
    
    public static Map<String, Float> getAllPlayerHealths() {
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
    
    public static void clearHealthData() {
        playerHealths.clear();
    }
}
