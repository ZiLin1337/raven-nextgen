package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;

public class Freelook extends Module {
    public static boolean perspectiveToggled;
    public static float cameraYaw;
    public static float cameraPitch;

    public Freelook() {
        super("Free Look", category.render);
    }
}
