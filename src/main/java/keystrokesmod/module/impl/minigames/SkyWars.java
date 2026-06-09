package keystrokesmod.module.impl.minigames;

import keystrokesmod.module.Module;
import keystrokesmod.script.model.Vec3d;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkyWars extends Module {
    public final Set<Integer> spawnedMobs = new HashSet<>();
    public final Map<String, SpawnEggInfo> entitySpawnQueue = new HashMap<>();

    public SkyWars() {
        super("SkyWars", category.minigames);
    }

    public static class SpawnEggInfo {
        public final Vec3d spawnPos;
        public final int tickSpawned;
        public SpawnEggInfo(Vec3d spawnPos, int tickSpawned) {
            this.spawnPos = spawnPos;
            this.tickSpawned = tickSpawned;
        }
    }
}
