package keystrokesmod.utility;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.util.*;

public class Utils implements IMinecraftInstance {
    public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("Raven");
    public static List<String> friends = new ArrayList<>();
    public static List<String> enemies = new ArrayList<>();

    public static void sendMessage(String message) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(message), false);
    }

    public static void sendRawMessage(String message) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(message), false);
    }

    public static boolean isMoving() {
        return mc.player != null && (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0);
    }

    public static void addFriend(String name) { friends.add(name); }
    public static void removeFriend(String name) { friends.remove(name); }
    public static boolean isFriended(String name) { return friends.contains(name); }
    public static void addEnemy(String name) { enemies.add(name); }
    public static void removeEnemy(String name) { enemies.remove(name); }
    public static boolean isEnemy(String name) { return enemies.contains(name); }

    public static void attackEntity(Object entity, boolean swing, boolean crits) {
    }

    public static double round(double value, int places) {
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }

    public static void setSpeed(double speed) {
    }

    public static boolean isDiagonal(boolean checkMoving) {
        return false;
    }

    public static int getChroma(long speed, long offset) {
        return 0xFFFFFF;
    }

    public static int mergeAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static float getEnum(Object enumObj) {
        return 0;
    }

    public static List<String> getSidebarLines() {
        return new ArrayList<>();
    }

    public static List<net.minecraft.client.network.PlayerListEntry> getTablist(boolean footer) {
        return new ArrayList<>();
    }

    public static void log(Object obj) {
    }

    public static void callScriptFunction(String scriptName, String functionName, Object... args) {
    }
}
