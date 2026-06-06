package keystrokesmod.script;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.script.model.Vec3;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;

/**
 * Bridges in-game events to JavaScript/script runtime.
 * Uses the client's Orbit event bus instead of MinecraftForge.
 */
public class ScriptEvents {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private String scriptName;

    public ScriptEvents(String scriptName) {
        this.scriptName = scriptName;
    }

    public static void register(Object listener) {
        Raven.getEventBus().subscribe(listener);
    }

    public static void unregister(Object listener) {
        Raven.getEventBus().unsubscribe(listener);
    }

    // ====== Event callback bridge methods ======

    public void onPreMotion(PreMotionEvent event) {
        if (mc.player == null) return;
        try {
            Utils.callScriptFunction(scriptName, "onPreMotion",
                    0f // event.getYaw(),
                    0f // event.getPitch(),
                    event.getPosX(),
                    event.getPosY(),
                    event.getPosZ(),
                    event.isOnGround(),
                    event.isSneaking());
        } catch (Exception ignored) {}
    }

    public void onPrePlayerInput(PrePlayerInputEvent event) {
        if (mc.player == null) return;
        try {
            Utils.callScriptFunction(scriptName, "onPrePlayerInput",
                    event.getForward(),
                    event.getStrafe(),
                    0f // event.getPitch(),
                    0f // event.getYaw(),
                    event.isJump(),
                    event.isSneak());
        } catch (Exception ignored) {}
    }

    public void onTick() {
        if (mc.player == null) return;
        try {
            // Utils.callScriptFunction(scriptName, "onTick"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onWorldLoad() {
        try {
            // Utils.callScriptFunction(scriptName, "onWorldLoad"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onKeyPress(int key) {
        try {
            // Utils.callScriptFunction(scriptName, "onKeyPress", key); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onClick(int mouseButton) {
        try {
            // Utils.callScriptFunction(scriptName, "onClick", mouseButton); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onPacketReceived(Object packet) {
        try {
            // Utils.callScriptFunction(scriptName, "onPacket", packet); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onPacketSent(Object packet) {
        try {
            // Utils.callScriptFunction(scriptName, "onSendPacket", packet); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onChat(String message) {
        try {
            // Utils.callScriptFunction(scriptName, "onChat", message); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onMove(double x, double y, double z) {
        try {
            // Utils.callScriptFunction(scriptName, "onMove", x, y, z); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onJump() {
        try {
            // Utils.callScriptFunction(scriptName, "onJump"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onStep() {
        try {
            // Utils.callScriptFunction(scriptName, "onStep"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onAttack(Object target) {
        try {
            // Utils.callScriptFunction(scriptName, "onAttack", target); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onDamage(float health, float damage) {
        try {
            // Utils.callScriptFunction(scriptName, "onDamage", health, damage); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onDeath() {
        try {
            // Utils.callScriptFunction(scriptName, "onDeath"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onRespawn() {
        try {
            // Utils.callScriptFunction(scriptName, "onRespawn"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onServerConnect(String server) {
        try {
            // Utils.callScriptFunction(scriptName, "onServerConnect", server); // TODO: script engine
        } catch (Exception ignored) {}
    }

    public void onServerDisconnect() {
        try {
            // Utils.callScriptFunction(scriptName, "onServerDisconnect"); // TODO: script engine
        } catch (Exception ignored) {}
    }

    /**
     * Enables all event listeners for this script
     */
    public void enable() {
        register(this);
    }

    /**
     * Disables all event listeners for this script
     */
    public void disable() {
        unregister(this);
    }
}
