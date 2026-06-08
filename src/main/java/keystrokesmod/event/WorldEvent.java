package keystrokesmod.event;

import net.minecraft.client.world.ClientWorld;

public class WorldEvent extends Event {
    public static class Load extends WorldEvent {
        public final ClientWorld world;

        public Load(ClientWorld world) {
            this.world = world;
        }
    }
}
