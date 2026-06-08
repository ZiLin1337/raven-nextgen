package keystrokesmod.helper;

import net.minecraft.client.MinecraftClient;

public class InputUtil {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isButtonDown(int button) {
        return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == 1;
    }

    public static double getX() {
        return mc.mouse.getX();
    }

    public static double getY() {
        return mc.mouse.getY();
    }

    public static int getScaledX() {
        return (int)(getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
    }

    public static int getScaledY() {
        return (int)(getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
    }

    public static int getDWheel() {
        return mc.mouse.getDWheel();
    }
}
