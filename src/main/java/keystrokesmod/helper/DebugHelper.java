package keystrokesmod.helper;

import keystrokesmod.Raven;
import net.minecraft.util.Util;

public class DebugHelper {
    public static void info(String msg) {
        if (Raven.DEBUG) System.out.println("[Raven Debug] " + msg);
    }

    public static void warn(String msg) {
        System.out.println("[Raven WARN] " + msg);
    }

    public static void error(String msg) {
        System.err.println("[Raven ERROR] " + msg);
    }

    public static String getMinecraftInfo() {
        return "Minecraft " + Util.getVersionType();
    }

    public static String getJavaInfo() {
        return System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
    }

    public static String getOSInfo() {
        return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")";
    }

    public static int getModuleCount() {
        return Raven.moduleManager.getModules().size();
    }

    public static int getEnabledCount() {
        return (int) Raven.moduleManager.getModules().stream().filter(m -> m.isEnabled()).count();
    }
}
