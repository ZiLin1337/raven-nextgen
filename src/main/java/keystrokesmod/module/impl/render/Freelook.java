package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.Freecam;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;

public class Freelook extends Module {
    public static boolean perspectiveToggled;
    public static float cameraYaw;
    public static float cameraPitch;
    private ButtonSetting hold;
    private ButtonSetting invertPitch;
    private ButtonSetting lockPitch;
    private ButtonSetting customFov;
    private SliderSetting fov;
    private KeySetting freelookKey;
    private boolean prevKeyState;
    private int previousPerspective;
    private float lastFov;

    public Freelook() {
        super("Free Look", category.render);
        this.registerSetting(freelookKey = new KeySetting("Key", 56));
        this.registerSetting(hold = new ButtonSetting("Hold", true));
        this.registerSetting(invertPitch = new ButtonSetting("Invert pitch", false));
        this.registerSetting(lockPitch = new ButtonSetting("Lock pitch", true));
        this.registerSetting(customFov = new ButtonSetting("Custom FOV", false));
        this.registerSetting(fov = new SliderSetting("FOV", 90, 10, 150, 1));
    }

    @Override
    public void guiUpdate() {
        fov.setVisible(customFov.isToggled(), this);
    }

    public void onTick() {
        if (mc.currentScreen != null || !Utils.nullCheck()) {
            return;
        }
        
        Module freecamMod = ModuleManager.getModule(Freecam.class);
        if (freecamMod instanceof Freecam && ((Freecam) freecamMod).freeEntity != null) {
            return;
        }

        boolean down = mc.options.keyUse.getDefault().isPressed() // keyUse;
        if (down != prevKeyState) {
            onPressed(down);
            prevKeyState = down;
        }
    }

    private void onPressed(boolean state) {
        if (!isEnabled()) {
            if (perspectiveToggled) {
                resetPerspective();
            }
            return;
        }

        if (state) {
            cameraYaw = mc.player.getYaw();
            cameraPitch = mc.player.getPitch();
            if (perspectiveToggled) {
                resetPerspective();
            } else {
                enterPerspective();
            }
        } else if (hold.isToggled()) {
            resetPerspective();
        }
    }

    private void enterPerspective() {
        perspectiveToggled = true;
        previousPerspective = mc.options.getPerspective().ordinal();
        applyThirdPersonView(1);
        lastFov = (float) mc.options.getFov().getValue();
    }

    public void resetPerspective() {
        perspectiveToggled = false;
        applyThirdPersonView(previousPerspective);
        // FOV restore TODO
    }

    public static boolean overrideMouse(MinecraftClient mc) {
        if (!mc.isWindowFocused()) {
            return false;
        }
        if (ModuleManager.freelook == null || !ModuleManager.freelook.isEnabled() || !perspectiveToggled) {
            return true;
        }
        // Mouse handling simplified for 1.21.4
        Freelook fl = ModuleManager.freelook;
        if (fl != null && fl.customFov.isToggled()) {
            // FOV setting TODO
        }
        return false;
    }

    @Override
    public void onDisable() {
        if (perspectiveToggled) {
            perspectiveToggled = false;
            applyThirdPersonView(0);
        }
    }

    private void applyThirdPersonView(int view) {
        if (view < 0) view = 0;
        if (view > 2) view = 2;
        
        switch (view) {
            case 0 -> mc.options.setPerspective(Perspective.FIRST_PERSON);
            case 1 -> mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            case 2 -> mc.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
        }
    }
}
