package keystrokesmod.helper;

import net.minecraft.client.MinecraftClient;

public class DebugHelper {
    public static String getMinecraftVersion() {
        return MinecraftClient.getInstance().getGameVersion();
    }
}