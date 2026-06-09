package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;

public class AntiShuffle extends Module {
    public AntiShuffle() {
        super("AntiShuffle", category.render);
    }

    public static String removeObfuscation(String text) {
        return text;
    }
}