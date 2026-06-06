package keystrokesmod.event;
import net.minecraft.entity.LivingEntity;

public class RenderLivingEvent extends Event {
    public static class Specials {
        public static class Pre extends RenderLivingEvent {
            private final LivingEntity entity;
            public Pre(LivingEntity entity) { this.entity = entity; }
            public LivingEntity getEntity() { return entity; }
        }
    }
}
