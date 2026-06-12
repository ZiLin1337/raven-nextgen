package keystrokesmod.utility.shader;

import org.lwjgl.opengl.GL11;

public class RoundedUtils {
    
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        
        // Draw rounded rectangle
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(x + radius, y);
        GL11.glVertex2f(x + width - radius, y);
        GL11.glVertex2f(x + width, y + radius);
        GL11.glVertex2f(x + width, y + height - radius);
        GL11.glVertex2f(x + width - radius, y + height);
        GL11.glVertex2f(x + radius, y + height);
        GL11.glVertex2f(x, y + height - radius);
        GL11.glVertex2f(x, y + radius);
        GL11.glEnd();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float lineWidth, int color) {
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(x + radius, y);
        GL11.glVertex2f(x + width - radius, y);
        GL11.glVertex2f(x + width, y + radius);
        GL11.glVertex2f(x + width, y + height - radius);
        GL11.glVertex2f(x + width - radius, y + height);
        GL11.glVertex2f(x + radius, y + height);
        GL11.glVertex2f(x, y + height - radius);
        GL11.glVertex2f(x, y + radius);
        GL11.glEnd();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
