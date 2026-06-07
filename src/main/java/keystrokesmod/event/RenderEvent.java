package keystrokesmod.event;

import net.minecraft.client.gui.DrawContext;

public class RenderEvent extends Event {
    private final DrawContext context;
    private final float tickDelta;
    
    public RenderEvent(DrawContext context, float tickDelta) {
        this.context = context;
        this.tickDelta = tickDelta;
    }
    
    public DrawContext getContext() { return context; }
    public float getTickDelta() { return tickDelta; }
}