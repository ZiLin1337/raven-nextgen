package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.mixin.impl.accessor.IAccessorMinecraft;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.math.BlockPos;

public class NoFall extends Module {
    public SliderSetting mode;
    private SliderSetting minFallDistance;
    private ButtonSetting disableAdventure;
    private ButtonSetting ignoreVoid;
    private String[] modes = new String[]{"Spoof", "NoGround", "Packet A", "Packet B"};

    private double initialY;
    private double dynamic;
    private boolean isFalling;

    public NoFall() {
        super("NoFall", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", 2, modes));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3, 0, 10, 0.1));
        this.registerSetting(disableAdventure = new ButtonSetting("Disable adventure", false));
        this.registerSetting(ignoreVoid = new ButtonSetting("Ignore void", true));
    }

    public void onDisable() {
        Utils.resetTimer();
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (reset()) {
            Utils.resetTimer();
            initialY = mc.player.getY();
            isFalling = false;
            return;
        }
        else if ((double) mc.player.fallDistance >= minFallDistance.getInput()) {
            isFalling = true;
        }
        double predictedY = mc.player.getY() + mc.player.motionY;
        double distanceFallen = initialY - predictedY;
        if (mc.player.motionY >= -1.0) {
            dynamic = 3.0;
        }
        if (mc.player.motionY < -1.0) {
            dynamic = 4.0;
        }
        if (mc.player.motionY < -2.0) {
            dynamic = 5.0;
        }
        if (isFalling && mode.getInput() == 2) {
            if (distanceFallen >= dynamic) {
                ((IAccessorMinecraft) mc).getTimer().timerSpeed = (0.7399789F + (float) Utils.randomizeDouble(-0.012, 0.012));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                initialY = mc.player.getY();
            }
        }
        if (isFalling && mode.getInput() == 3) {
            if (mc.player.ticksExisted % 2 == 0) {
                ((IAccessorMinecraft) mc).getTimer().timerSpeed = (float) Utils.randomizeDouble(0.5, 0.50201);
            }
            else {
                ((IAccessorMinecraft) mc).getTimer().timerSpeed = (float) 1;
            }
            if (distanceFallen >= 3) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                initialY = mc.player.getY();
            }
        }
    }

    (priority = )
    public void onPreMotion(PreMotionEvent e) {
        switch ((int) mode.getInput()) {
            case 0:
                e.setOnGround(true);
                break;
            case 1:
                e.setOnGround(false);
                break;
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isVoid() {
        return Utils.overVoid(mc.player.getX(), mc.player.getY(), mc.player.getZ());
    }

    private boolean reset() {
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return true;
        }
        if (ignoreVoid.isToggled() && isVoid()) {
            return true;
        }
        if (Utils.isBedwarsPracticeOrReplay()) {
            return true;
        }
        if (Utils.spectatorCheck()) {
            return true;
        }
        if (mc.player.onGround) {
            return true;
        }
        if (BlockUtils.getBlock(new BlockPos(mc.player.getX(), mc.player.getY() - 1, mc.player.getZ())) != Blocks.AIR) {
            return true;
        }
        if (mc.player.motionY > -0.0784) {
            return true;
        }
        if (mc.player.capabilities.isCreativeMode) {
            return true;
        }
        if (isVoid() && mc.player.getY() <= 41) {
            return true;
        }
        if (mc.player.capabilities.isFlying) {
            return true;
        }
        return false;
    }
}