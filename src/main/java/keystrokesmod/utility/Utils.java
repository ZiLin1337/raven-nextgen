package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;

import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class Utils implements IMinecraftInstance {
    public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("Raven");
    public static List<String> friends = new ArrayList<>();
    public static List<String> enemies = new ArrayList<>();
    public static HashSet<String> friendsSet = new HashSet<>();
    public static HashSet<String> enemiesSet = new HashSet<>();

    // ==================== 消息工具 ====================
    public static void sendMessage(String message) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(message), false);
    }

    public static void sendRawMessage(String message) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(message), false);
    }

    // ==================== 移动工具 ====================
    public static boolean isMoving() {
        return mc.player != null && (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0);
    }

    public static boolean isDiagonal(boolean checkMoving) {
        if (mc.player == null) return false;
        boolean forward = mc.player.input.movementForward != 0;
        boolean sideways = mc.player.input.movementSideways != 0;
        return forward && sideways;
    }

    public static void setSpeed(double speed) {
        if (mc.player == null) return;
        float yaw = mc.player.getYaw();
        float forward = 1.0F;
        float strafe = 0.0F;
        if (mc.player.input.movementForward < 0) {
            yaw += 180;
            forward = -1.0F;
        }
        if (mc.player.input.movementSideways > 0.0F) yaw += 90.0F * forward;
        if (mc.player.input.movementSideways < 0.0F) yaw -= 90.0F * forward;
        double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        mc.player.setVelocity(cos * speed * forward, mc.player.getVelocity().y, sin * speed * forward);
    }

    // ==================== 好友/敌人 ====================
    public static boolean addFriend(String name) {
        if (enemiesSet.remove(name.toLowerCase())) sendMessage("&7Removed enemy &b" + name);
        return friendsSet.add(name.toLowerCase());
    }
    public static boolean removeFriend(String name) { return friendsSet.remove(name.toLowerCase()); }
    public static boolean isFriended(String name) { return friendsSet.contains(name.toLowerCase()); }
    public static boolean addEnemy(String name) {
        if (friendsSet.remove(name.toLowerCase())) sendMessage("&7Removed friend &b" + name);
        return enemiesSet.add(name.toLowerCase());
    }
    public static boolean removeEnemy(String name) { return enemiesSet.remove(name.toLowerCase()); }
    public static boolean isEnemy(String name) { return enemiesSet.contains(name.toLowerCase()); }

    // ==================== 相机工具 ====================
    public static float getCameraYaw() {
        if (mc.player == null) return 0;
        return mc.player.getYaw();
    }

    public static float getCameraPitch() {
        if (mc.player == null) return 0;
        return mc.player.getPitch();
    }

    public static Vec3d getCameraPos() {
        if (mc.player == null) return Vec3d.ZERO;
        return mc.player.getEyePos();
    }

    public static Vec3d getLookVec() {
        if (mc.player == null) return Vec3d.ZERO;
        float yaw = (float) Math.toRadians(mc.player.getYaw());
        float pitch = (float) Math.toRadians(mc.player.getPitch());
        return new Vec3d(
            -Math.sin(yaw) * Math.cos(pitch),
            -Math.sin(pitch),
            Math.cos(yaw) * Math.cos(pitch)
        );
    }

    // ==================== 距离/射线工具 ====================
    public static double getDistanceToEye(Entity entity) {
        if (mc.player == null || entity == null) return Double.MAX_VALUE;
        return entity.getEyePos().distanceTo(mc.player.getEyePos());
    }

    public static double raycastDistanceSq(Entity entity, double maxReach, boolean calcRotation) {
        if (mc.player == null || entity == null) return Double.MAX_VALUE;
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
        return eyePos.squaredDistanceTo(entityPos);
    }

    // ==================== 视线检查 ====================
    public static boolean canSeeVec(Vec3d from, Vec3d to) {
        if (mc.world == null) return false;
        BlockHitResult result = mc.world.raycast(new net.minecraft.world.RaycastContext(
            from, to,
            net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
            net.minecraft.world.RaycastContext.FluidHandling.NONE,
            mc.player
        ));
        return result.getType() == HitResult.Type.MISS;
    }

    public static boolean canPlayerBeSeen(LivingEntity player) {
        if (mc.player == null || player == null) return false;
        return canSeeVec(mc.player.getEyePos(), player.getEyePos());
    }

    // ==================== FOV检查 ====================
    public static boolean inFov(float fov, BlockPos blockPos) {
        if (mc.player == null) return false;
        float angle = getAngleToBlock(blockPos);
        return Math.abs(angle) < fov / 2.0f;
    }

    public static boolean inFov(float fov, Entity entity) {
        if (mc.player == null || entity == null) return false;
        float angle = getAngleToEntity(entity);
        return Math.abs(angle) < fov / 2.0f;
    }

    public static boolean inFov(float origin, float fov, float targetYaw) {
        float diff = Math.abs(origin - targetYaw) % 360;
        if (diff > 180) diff = 360 - diff;
        return diff < fov / 2.0f;
    }

    private static float getAngleToBlock(BlockPos pos) {
        if (mc.player == null) return 180;
        double diffX = pos.getX() + 0.5 - mc.player.getX();
        double diffZ = pos.getZ() + 0.5 - mc.player.getZ();
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90);
        return wrapAngleTo180(yaw - mc.player.getYaw());
    }

    private static float getAngleToEntity(Entity entity) {
        if (mc.player == null) return 180;
        double diffX = entity.getX() - mc.player.getX();
        double diffZ = entity.getZ() - mc.player.getZ();
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90);
        return wrapAngleTo180(yaw - mc.player.getYaw());
    }

    public static float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    // ==================== 虚空检查 ====================
    public static boolean overVoid(double x, double y, double z) {
        if (mc.world == null) return false;
        if (y > 0) return false;
        for (int i = (int) y; i > -64; i--) {
            if (!mc.world.getBlockState(new BlockPos((int) x, i, (int) z)).isAir()) return false;
        }
        return true;
    }

    // ==================== 实体检查 ====================
    public static boolean isConsuming(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return player.isUsingItem();
        }
        return false;
    }

    public static boolean holdingFood(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            Item item = player.getMainHandStack().getItem();
            return item.getComponents().contains(DataComponentTypes.FOOD);
        }
        return false;
    }

    public static boolean holdingBow() {
        if (mc.player == null) return false;
        return mc.player.getMainHandStack().getItem() instanceof BowItem;
    }

    public static boolean holdingFireball() {
        if (mc.player == null) return false;
        return mc.player.getMainHandStack().getItem() == Items.FIRE_CHARGE;
    }

    public static boolean isInTabList(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) return false;
        return mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) != null;
    }

    public static int getColorFromEntity(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return isEnemy(entity.getName().getString()) ? 0xFFFF0000 : 0xFF00FF00;
        }
        return 0xFFFFFFFF;
    }

    // ==================== 服务器信息 ====================
    public static String getServerName() {
        if (mc.getNetworkHandler() == null) return "Singleplayer";
        String serverAddress = mc.getNetworkHandler().getServerInfo().address;
        return serverAddress != null ? serverAddress : "Unknown";
    }

    public static boolean tabbedIn() {
        return mc.currentScreen == null || mc.currentScreen instanceof DisconnectedScreen;
    }

    // ==================== Tab列表 ====================
    public static List<PlayerListEntry> getTablist(boolean removeSelf) {
        if (mc.getNetworkHandler() == null) return new ArrayList<>();
        return mc.getNetworkHandler().getPlayerList().stream()
            .filter(entry -> !removeSelf || !entry.getProfile().getId().equals(mc.player.getUuid()))
            .collect(Collectors.toList());
    }

    public static List<String> getSidebarLines() {
        List<String> lines = new ArrayList<>();
        return lines;
    }

    // ==================== 工具方法 ====================
    public static double round(double value, int places) {
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }

    public static int randomizeInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static double randomizeDouble(double min, double max) {
        return min + (max - min) * new Random().nextDouble();
    }

    public static boolean isWholeNumber(double num) {
        return num == Math.floor(num);
    }

    public static String asWholeNum(double input) {
        return String.valueOf((int) input);
    }

    public static int getChroma(long speed, long offset) {
        float hue = (float) ((System.currentTimeMillis() + offset) % speed) / (float) speed;
        return java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
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

    public static void log(Object obj) {
        log.info(String.valueOf(obj));
    }

    public static void callScriptFunction(String scriptName, String functionName, Object... args) {}

    public static void attackEntity(Object entity, boolean swing, boolean crits) {}
}
