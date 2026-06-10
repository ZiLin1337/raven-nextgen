package keystrokesmod.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.util.*;

public class ModuleUtils implements IMinecraftInstance {
    public static boolean isMoving() {
        return mc.player != null && (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0);
    }

    public static boolean isHoldingSword() {
        return false;
    }

    public static boolean isBlocking() {
        return false;
    }

    public static boolean isOnGround() {
        return mc.player != null && mc.player.isOnGround();
    }

    public static boolean isInWater() {
        return mc.player != null && mc.player.isTouchingWater();
    }

    public static boolean isInLava() {
        return mc.player != null && mc.player.isInLava();
    }

    public static boolean isFlying() {
        return mc.player != null && mc.player.getAbilities().flying;
    }

    public static boolean isSprinting() {
        return mc.player != null && mc.player.isSprinting();
    }

    public static boolean isSneaking() {
        return mc.player != null && mc.player.isSneaking();
    }

    public static boolean isRiding() {
        return mc.player != null && mc.player.hasVehicle();
    }

    public static boolean isCreative() {
        return mc.player != null && mc.player.getAbilities().creativeMode;
    }

    public static boolean isSpectator() {
        return mc.player != null && mc.player.isSpectator();
    }
}