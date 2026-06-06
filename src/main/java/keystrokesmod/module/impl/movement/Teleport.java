package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.event.ClickMouseEvent;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

public class Teleport extends Module {
    private ButtonSetting rightClick;
    private ButtonSetting highlightTarget;
    private ButtonSetting highlightPath;
    private ButtonSetting instant;

    private BlockPos targetPos;
    private ArrayList<Vec3d> path = new ArrayList<>();

    public Teleport() {
        super("Teleport", category.movement);
        this.registerSetting(rightClick = new ButtonSetting("Right click teleport", true));
        this.registerSetting(highlightTarget = new ButtonSetting("Highlight target", true));
        this.registerSetting(highlightPath = new ButtonSetting("Highlight path", false));
        this.registerSetting(instant = new ButtonSetting("Instant", false));
    }

    public void teleport(BlockPos targetBlock, boolean sendMessage) {
        targetBlock = targetBlock.up(1);
        int packetsSent = 0;
        if (!instant.isToggled()) {
            ArrayList<Vec3d> pathList = this.path = getPath(targetBlock);
            for (Vec3d pathPos : pathList) {
                mc.getNetHandler().networkHandler.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(pathPos.xCoord, pathPos.yCoord, pathPos.zCoord, true));
                if (++packetsSent >= 175) {
                    if (sendMessage) {
                        Utils.sendMessage("&eToo many packets, ending loop.");
                        break;
                    }
                    break;
                }
            }
        }
        mc.player.setPosition(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        if (sendMessage) {
            Utils.sendMessage("&eTeleported to &d(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ")" + (!instant.isToggled() ? " &ewith &b" + packetsSent + " &epackets." : "&e."));
        }
    }

    
    public void onRenderWorld(Object e) {
        if (!rightClick.isToggled() || !highlightTarget.isToggled() || this.targetPos == null || !Utils.nullCheck()) {
            return;
        }
        RenderUtils.renderBlock(targetPos, Color.orange.getRGB(), true, true);
        if (highlightPath.isToggled() && !instant.isToggled()) {
            int positions = 0;
            for (Vec3d pos : this.path) {
                if (positions >= 175) {
                    break;
                }
                RenderUtils.renderBlock(new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord), Color.yellow.getRGB(), false, true);
                ++positions;
            }
        }
    }

    private ArrayList getPath(BlockPos target) {
        ArrayList<Vec3d> path = new ArrayList<>();
        double newX = (double)target.getX() + 0.5;
        double newY = target.getY() + 1;
        double newZ = (double)target.getZ() + 0.5;
        double distance = this.mc.player.getDistance(newX, newY, newZ);
        double d = 0;
        while (d < distance) {
            path.add(new Vec3d(this.mc.player.getX() + (newX - (double)this.mc.player.getHorizontalFacing().getFrontOffsetX() - this.mc.player.getX()) * d / distance, this.mc.player.getY() + (newY - this.mc.player.getY()) * d / distance, this.mc.player.getZ() + (newZ - (double)this.mc.player.getHorizontalFacing().getFrontOffsetZ() - this.mc.player.getZ()) * d / distance));
            d += 2.0;
        }
        return path;
    }

    
    public void onMouse(ClickMouseEvent mouseEvent) {
        if (mouseEvent.button != 1 || !mouseEvent.buttonstate || !rightClick.isToggled() || !Utils.nullCheck()) {
            return;
        }
        HitResult rayCast = RotationUtils.rayCast(150.0, mc.player.getYaw(), mc.player.getPitch(), true);
        if (rayCast == null || rayCast.typeOfHit != HitResult.MovingObjectType.BLOCK) {
            return;
        }
        final BlockPos getBlockPos = rayCast.getBlockPos();
        this.targetPos = getBlockPos;
        teleport(getBlockPos, true);
    }

    @Override
    public void onEnable() {
        this.targetPos = null;
        this.path.clear();
        if (rightClick.isToggled()) {
            return;
        }
        HitResult rayCast = RotationUtils.rayCast(150.0, mc.player.getYaw(), mc.player.getPitch(), true);
        if (rayCast == null || rayCast.typeOfHit != HitResult.MovingObjectType.BLOCK) {
            return;
        }
        teleport(rayCast.getBlockPos(), true);
        this.disable();
    }
}