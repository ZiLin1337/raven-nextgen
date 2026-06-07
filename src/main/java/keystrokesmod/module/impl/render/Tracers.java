package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;




import java.util.ArrayList;

public class Tracers extends Module {
    public ButtonSetting showInvis;
    public ColorSetting color;
    public ButtonSetting rainbow;
    public SliderSetting lineWidth;

    private boolean viewBobbingEnabled;
    private final ArrayList<Entity> trackedEntities = new ArrayList<>();
    private int trackedEntityCount = 0;

    public Tracers() {
        super("Tracers", category.render);
        this.registerSetting(showInvis = new ButtonSetting("Show invis", true));
        this.registerSetting(lineWidth = new SliderSetting("Line Width", 1.0D, 1.0D, 5.0D, 1.0D));
        this.registerSetting(color = new ColorSetting("Color", 0, 255, 0));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", false));
    }

    @Override
    public void onEnable() {
        this.viewBobbingEnabled = mc.options.viewBobbing;
        if (this.viewBobbingEnabled) {
            mc.options.viewBobbing = false;
        }
    }

    @Override
    public void onDisable() {
        mc.options.viewBobbing = this.viewBobbingEnabled;
    }

    @Override
    public void onUpdate() {
        if (mc.options.viewBobbing) {
            mc.options.viewBobbing = false;
        }
    }

    
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        updateTrackedEntities();
    }

    
    public void onRenderWorldLast(RenderWorldLastEvent e) {
        if (!Utils.nullCheck() || trackedEntityCount == 0) {
            return;
        }
        int rgb = rainbow.isToggled() ? Utils.getChroma(2L, 0L) : color.getColor();
        for (int i = 0; i < trackedEntityCount; i++) {
            Entity entity = trackedEntities.get(i);
            if (entity == null) {
                continue;
            }
            RenderUtils.drawTracerLine(entity, rgb, (float) lineWidth.getInput(), e.partialTicks);
        }
    }

    private void updateTrackedEntities() {
        trackedEntityCount = 0;
        if (!Utils.nullCheck() || mc.world == null) {
            return;
        }

        if (Raven.DEBUG) {
            for (Entity entity : mc.world.getEntities() {
                if (entity instanceof LivingEntity && entity != mc.player) {
                    addTrackedEntity(entity);
                }
            }
            return;
        }

        for (PlayerEntity player : mc.world.getPlayers() {
            if (player == mc.player) {
                continue;
            }
            if (player.deathTime != 0) {
                continue;
            }
            if (!showInvis.isToggled() && player.isInvisible() {
                continue;
            }
            if (AntiBot.isBot(player) {
                continue;
            }
            addTrackedEntity(player);
        }
    }

    private void addTrackedEntity(Entity entity) {
        if (trackedEntityCount >= trackedEntities.size() {
            trackedEntities.add(entity);
        }
        else {
            trackedEntities.set(trackedEntityCount, entity);
        }
        trackedEntityCount++;
    }
}
