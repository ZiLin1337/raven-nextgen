package keystrokesmod.module.impl.movement;

import keystrokesmod.event.CollisionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.util.math.Box;

import org.apache.commons.lang3.RandomUtils;

public class Fly extends Module {
    private SliderSetting mode;
    public static SliderSetting horizontalSpeed;
    private SliderSetting verticalSpeed;
    private ButtonSetting showBPS;
    private ButtonSetting stopMotion;
    private ButtonSetting keepY;

    private String[] modes = new String[]{"Vanilla", "Fast", "Fast 2", "Walk"};

    private boolean canFly;
    private int maxY;

    private Box FULL_ABB = new Box(0, 0, 0, 1.0D,1.0D,1.0D);

    public Fly() {
        super("Fly", category.movement);
        this.registerSetting(mode = new SliderSetting("Fly", 0, modes));
        this.registerSetting(horizontalSpeed = new SliderSetting("Horizontal speed", 2.0, 1.0, 9.0, 0.1));
        this.registerSetting(verticalSpeed = new SliderSetting("Vertical speed", 2.0, 1.0, 9.0, 0.1));
        this.registerSetting(showBPS = new ButtonSetting("Show BPS", false));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
        this.registerSetting(keepY = new ButtonSetting("Keep-Y", false));
    }

    @Override
    public void guiUpdate() {
        this.horizontalSpeed.setVisible(mode.getInput() != 3, this);
        this.verticalSpeed.setVisible(mode.getInput() != 3, this);

        this.keepY.setVisible(mode.getInput() == 3, this);
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    @Override
    public void onEnable() {
        this.canFly = mc.player.capabilities.isFlying;
        this.maxY = mc.player.getPosition().getY();
    }

    
    public void onCollision(CollisionEvent e) {
        if (mode.getInput() != 3 || Utils.isBindDown(mc.options.keyBindSneak)) {
            this.maxY = mc.player.getPosition().getY();
            return;
        }
        if (e.blockPos.getY() < (this.keepY.isToggled() ? maxY : mc.player.getY())) {
            e.boundingBox = FULL_ABB.offset(e.blockPos.getX(), e.blockPos.getY(), e.blockPos.getZ());
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getInput() == 3) {
            return;
        }
        switch ((int) mode.getInput()) {
            case 0:
                mc.player.motionY = 0.0;
                mc.player.capabilities.setFlySpeed((float)(0.05000000074505806 * horizontalSpeed.getInput()));
                mc.player.capabilities.isFlying = true;
                break;
            case 1:
                mc.player.onGround = true;
                if (mc.currentScreen == null) {
                    if (Utils.jumpDown()) {
                        mc.player.motionY = 0.3 * verticalSpeed.getInput();
                    }
                    else if (Utils.jumpDown()) {
                        mc.player.motionY = -0.3 * verticalSpeed.getInput();
                    }
                    else {
                        mc.player.motionY = 0.0;
                    }
                }
                else {
                    mc.player.motionY = 0.0;
                }
                mc.player.capabilities.setFlySpeed(0.2f);
                mc.player.capabilities.isFlying = true;
                setSpeed(0.85 * horizontalSpeed.getInput());
                break;
            case 2:
                double nextDouble = RandomUtils.nextDouble(1.0E-7, 1.2E-7);
                if (mc.player.ticksExisted % 2 == 0) {
                    nextDouble = -nextDouble;
                }
                if (!mc.player.onGround) {
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() + nextDouble, mc.player.getZ());
                }
                mc.player.motionY = 0.0;
                setSpeed(0.4 * horizontalSpeed.getInput());
                break;
        }

    }

    @Override
    public void onDisable() {
        if (mc.player.capabilities.allowFlying) {
            mc.player.capabilities.isFlying = this.canFly;
        }
        else {
            mc.player.capabilities.isFlying = false;
        }
        this.canFly = false;
        switch ((int) mode.getInput()) {
            case 0:
            case 1:
                mc.player.capabilities.setFlySpeed(0.05F);
                break;
        }
        if (stopMotion.isToggled()) {
            mc.player.motionZ = 0;
            mc.player.motionY = 0;
            mc.player.motionX = 0;
        }
    }

    
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (!showBPS.isToggled() || e.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen != null || mc.options.showDebugInfo) {
            return;
        }
        RenderUtils.renderBPS(true, false);
    }

    private void setSpeed(double speed) {
        if (speed == 0.0) {
            mc.player.motionZ = 0;
            mc.player.motionX = 0;
            return;
        }
        double moveForward = mc.player.movementInput.moveForward;
        double moveStrafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (moveForward == 0.0 && moveStrafe == 0.0) {
            mc.player.motionZ = 0;
            mc.player.motionX = 0;
        }
        else {
            if (moveForward != 0.0) {
                if (moveStrafe > 0.0) {
                    yaw += ((moveForward > 0.0) ? -45 : 45);
                }
                else if (moveStrafe < 0.0) {
                    yaw += ((moveForward > 0.0) ? 45 : -45);
                }
                moveStrafe = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                }
                else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }
            double radians = Math.toRadians(yaw + 90.0f);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            mc.player.motionX = moveForward * speed * cos + moveStrafe * speed * sin;
            mc.player.motionZ = moveForward * speed * sin - moveStrafe * speed * cos;
        }
    }
}