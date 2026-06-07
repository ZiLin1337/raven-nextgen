package keystrokesmod.module.impl.world;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3dd;

public class Scaffold extends Module {
    private SliderSetting extend;
    private ButtonSetting silentRotation, tower, autoSwap;
    private BlockPos targetBlock;
    private Direction targetFace;

    public Scaffold() {
        super("Scaffold", category.world);
        this.registerSetting(extend = new SliderSetting("Extend", 0, 0, 5, 1));
        this.registerSetting(silentRotation = new ButtonSetting("Silent rotation", true));
        this.registerSetting(tower = new ButtonSetting("Tower", true));
        this.registerSetting(autoSwap = new ButtonSetting("Auto swap", true));
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (mc.player == null || mc.world == null) return;
        
        // Tower
        if (tower.isToggled() && mc.options.jumpKey.isPressed() && !mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
        }

        // Auto swap to blocks
        if (autoSwap.isToggled() && !(mc.player.getMainHandStack().getItem() instanceof BlockItem)) {
            if (!swapToBlocks()) return;
        }

        // Find place position
        BlockPos playerPos = mc.player.getBlockPos();
        int extendVal = (int) extend.getInput();
        
        // Look for block below player
        targetBlock = null;
        for (int x = -extendVal; x <= extendVal; x++) {
            for (int z = -extendVal; z <= extendVal; z++) {
                BlockPos below = playerPos.add(x, -1, z);
                if (mc.world.getBlockState(below).isAir()) {
                    // Find a face to place against
                    for (Direction face : Direction.values()) {
                        BlockPos neighbor = below.offset(face);
                        if (!mc.world.getBlockState(neighbor).isAir()) {
                            targetBlock = below;
                            targetFace = face.getOpposite();
                            break;
                        }
                    }
                }
                if (targetBlock != null) break;
            }
            if (targetBlock != null) break;
        }

        if (targetBlock == null) return;

        // Rotate toward block
        Vec3dd center = Vec3dd.ofCenter(targetBlock);
        float[] rots = RotationUtils.getRotationsTo(center.x, center.y, center.z);
        e.setYaw(rots[0]);
        e.setPitch(rots[1]);

        // Place block
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, 
            new BlockHitResult(Vec3dd.ofCenter(targetBlock), targetFace, targetBlock, false));
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean swapToBlocks() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().main.get(i).getItem() instanceof BlockItem) {
                mc.player.getInventory().selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    public void onDisable() { targetBlock = null; }
}
