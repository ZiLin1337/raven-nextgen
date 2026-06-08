package keystrokesmod.event;

import net.minecraft.entity.Entity;
import net.minecraft.client.world.ClientWorld;

public class EntityJoinWorldEvent extends Event {
    public final Entity entity;
    public final ClientWorld world;

    public EntityJoinWorldEvent(Entity entity, ClientWorld world) {
        this.entity = entity;
        this.world = world;
    }
}
