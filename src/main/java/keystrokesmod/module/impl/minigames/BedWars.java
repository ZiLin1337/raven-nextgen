package keystrokesmod.module.impl.minigames;

import keystrokesmod.module.Module;
import java.util.HashSet;
import java.util.Set;

public class BedWars extends Module {
    public final Set<Integer> spawnedMobs = new HashSet<>();

    public BedWars() {
        super("BedWars", category.minigames);
    }
}
