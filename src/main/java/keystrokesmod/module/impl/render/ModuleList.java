package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;

public class ModuleList extends Module {
    private SliderSetting x, y, gap;

    public ModuleList() {
        super("ModuleList", category.render);
        this.registerSetting(x = new SliderSetting("X", 2, 0, mc.getWindow().getScaledWidth(), 1));
        this.registerSetting(y = new SliderSetting("Y", 30, 0, mc.getWindow().getScaledHeight(), 1));
        this.registerSetting(gap = new SliderSetting("Gap", 10, 2, 20, 1));
    }

    public void onRenderOverlay(DrawContext context) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        java.util.List<Module> enabled = Raven.moduleManager.getModules().stream()
                .filter(m -> m.isEnabled() && !m.moduleCategory().equals(Module.category.client))
                .sorted(Comparator.comparing(m -> -tr.getWidth(m.getName()))).toList();
        int yPos = (int) y.getInput();
        int xPos = (int) x.getInput();
        for (Module m : enabled) {
            int color = keystrokesmod.utility.Utils.getChroma(2L, System.currentTimeMillis() / 50);
            context.drawText(tr, m.getName(), xPos, yPos, color, true);
            yPos += gap.getInput();
        }
    }
}
