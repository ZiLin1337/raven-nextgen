package keystrokesmod.module.impl.world;

import keystrokesmod.module.Module;
import net.minecraft.entity.Entity;

public class AntiBot extends Module {
    public AntiBot() {
        super("Anti Bot", Module.category.world, 0);
    }

    public static boolean isBot(Entity entity) {
        return false;
    }
}