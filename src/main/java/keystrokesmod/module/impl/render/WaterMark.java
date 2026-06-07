package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class WaterMark extends Module {
    private SliderSetting x, y;

    public WaterMark() {
        super("WaterMark", category.render);
        this.registerSetting(x = new SliderSetting("X", 2, 0, mc.getWindow().getScaledWidth(), 1));
        this.registerSetting(y = new SliderSetting("Y", 2, 0, mc.getWindow().getScaledHeight(), 1));
    }

    public void onRenderOverlay(DrawContext context) {
        TextRenderer tr = mc.textRenderer;
        String text = "Raven NextGen [1.21.4]";
        context.drawText(tr, text, (int)x.getInput(), (int)y.getInput(), 0xFF00FF00, true);
    }
}
