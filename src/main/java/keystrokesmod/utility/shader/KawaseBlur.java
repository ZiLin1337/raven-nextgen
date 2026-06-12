package keystrokesmod.utility.shader;

public class KawaseBlur {
    private static boolean initialized = false;
    
    public static void init() {
        initialized = true;
    }
    
    public static void render(float radius) {
        // Kawase blur effect
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
