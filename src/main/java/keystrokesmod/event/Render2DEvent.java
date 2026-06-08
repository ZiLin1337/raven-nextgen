package keystrokesmod.event;

import net.minecraft.client.gui.DrawContext;

/**
 * 1.21.4 compatible Render2D event
 */
public class Render2DEvent extends Event {
    public final DrawContext drawContext;
    public final int screenWidth;
    public final int screenHeight;
    public final float tickDelta;
    
    public Render2DEvent(DrawContext drawContext, int screenWidth, int screenHeight, float tickDelta) {
        this.drawContext = drawContext;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tickDelta = tickDelta;
    }
}
