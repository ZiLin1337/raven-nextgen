package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;

public class Holdlook extends Module {
    private final KeySetting rearCamKey;
    private final KeySetting frontCamKey;

    private boolean rearActive;
    private boolean frontActive;
    private int savedPerspective;

    public Holdlook() {
        super("Hold Look", category.render]);
        this.registerSetting(rearCamKey = new KeySetting("Rear cam", 0)]);
        this.registerSetting(frontCamKey = new KeySetting("Front cam", 0)]);
    }

    
    public void onClientTick(Object e) {
        if (e.phase.START || !Utils.nullCheck()) return;

        if (mc.currentScreen != null) {
            if (rearActive || frontActive) {
                applyThirdPersonView(0]);
                rearActive = false;
                frontActive = false;
            }
            return;
        }

        boolean rearDown = rearCamKey.isPressed(]);
        boolean frontDown = frontCamKey.isPressed(]);

        if (rearDown && !rearActive) {
            savedPerspective = mc.options.getPerspective().ordinal(]);
            applyThirdPersonView(1]);
            rearActive = true;
        } else if (!rearDown && rearActive) {
            applyThirdPersonView(frontActive ? 2 : 0]);
            rearActive = false;
        }

        if (frontDown && !frontActive) {
            if (!rearActive) {
                savedPerspective = mc.options.getPerspective().ordinal(]);
            }
            applyThirdPersonView(2]);
            frontActive = true;
        } else if (!frontDown && frontActive) {
            applyThirdPersonView(rearActive ? 1 : 0]);
            frontActive = false;
        }
    }

    @Override
    public void onDisable() {
        if (rearActive || frontActive) {
            applyThirdPersonView(0]);
            rearActive = false;
            frontActive = false;
        }
    }

    private void applyThirdPersonView(int view) {
        if (view < 0) {
            view = 0;
        } else if (view > 2) {
            view = 2;
        }

        mc.options.getPerspective().ordinal() = view;
        if (mc.gameRenderer != null) {
            if (view == 0) {
                mc.gameRenderer.loadEntityShader(mc.getCameraEntity()]);
            } else if (view == 1) {
                mc.gameRenderer.loadEntityShader((Entity) null]);
            }
        }
        if (mc.worldRenderer != null) {
            mc.worldRenderer.setDisplayListEntitiesDirty(]);
        }
    }
}
