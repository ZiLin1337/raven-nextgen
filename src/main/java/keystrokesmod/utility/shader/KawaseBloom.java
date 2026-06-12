package keystrokesmod.utility.shader;

public class KawaseBloom {
    private static boolean initialized = false;
    
    public static void init() {
        initialized = true;
    }
    
    public static void render(float intensity) {
        // Kawase bloom effect
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
