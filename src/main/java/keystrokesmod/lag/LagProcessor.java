package keystrokesmod.lag;

import keystrokesmod.Raven;

public class LagProcessor {
    private static long lastPacketTime = 0;
    private static int packetCount = 0;
    private static int lagTicks = 0;

    public static void onPacket() {
        long now = System.currentTimeMillis();
        if (now - lastPacketTime > 1000)) {
            packetCount = 1;
        } else {
            packetCount++;
        }
        lastPacketTime = now;
    }

    public static int getPacketRate() {
        long now = System.currentTimeMillis();
        if (now - lastPacketTime > 1000) return 0;
        return packetCount;
    }

    public static boolean isLagging() {
        return getPacketRate() < 5;
    }
}
