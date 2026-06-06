package keystrokesmod.utility;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.EnumDyeColor;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class BlockUtils implements IMinecraftInstance {
    public static boolean isSamePos(BlockPos blockPos, BlockPos blockPos2) {
        return blockPos == blockPos2 || (blockPos.getX() == blockPos2.getX() && blockPos.getY() == blockPos2.getY() && blockPos.getZ() == blockPos2.getZ());
    }

    public static boolean notFull(Block block) {
        return block instanceof BlockFenceGate || block instanceof BlockLadder || block instanceof BlockFlowerPot || block instanceof BlockBasePressurePlate || isFluid(block) || block instanceof BlockFence || block instanceof BlockAnvil || block instanceof BlockEnchantmentTable || block instanceof BlockChest;
    }

    public static boolean isNormalBlock(final Block block) {
        return block == Blocks.glass || (block.isFullBlock() && block != Blocks.gravel && block != Blocks.sand && block != Blocks.soul_sand && block != Blocks.tnt && block != Blocks.crafting_table && block != Blocks.furnace && block != Blocks.dispenser && block != Blocks.dropper && block != Blocks.noteblock && block != Blocks.command_block);
    }

    public static BlockPos pos(final double x, final double y, final double z) {
        return new BlockPos(x, y, z);
    }

    public static boolean isBlockPosEqual(final BlockPos pos1, final BlockPos pos2) {
        return pos1 == pos2 || (pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ());
    }

    public static BlockPos offsetPos(HitResult mop) {
        return mop.getBlockPos().offset(mop.sideHit);
    }

    public static boolean isFluid(Block block) {
        return block.getMaterial() == Material.lava || block.getMaterial() == Material.water;
    }

    public static boolean isInteractable(Block block) {
        return block instanceof BlockTrapDoor || block instanceof BlockDoor || block instanceof BlockContainer || block instanceof BlockJukebox || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BedBlock || block instanceof BlockDropper || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block instanceof BlockNote || block instanceof BlockWorkbench;
    }

    public static boolean isInteractable(HitResult mv) {
        if (mv == null || mv.typeOfHit != HitResult.MovingObjectType.BLOCK || mv.getBlockPos() == null) {
            return false;
        }
        if (!mc.player.isSneaking() || mc.player.getHeldItem() == null) {
            return isInteractable(BlockUtils.getBlock(mv.getBlockPos()));
        }
        return false;
    }

    public static float getBlockHardness(final Block block, final ItemStack itemStack, boolean ignoreSlow, boolean ignoreGround) {
        final float getBlockHardness = block.getBlockHardness(mc.world, null);
        if (getBlockHardness < 0.0f) {
            return 0.0f;
        }
        return (block.getMaterial().isToolNotRequired() || (itemStack != null && itemStack.canHarvestBlock(block))) ? (getToolDigEfficiency(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness / 30.0f) : (getToolDigEfficiency(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness / 100.0f);
    }

    public static float maxDigRateAcrossSlots(Block block, int slotCount) {
        if (mc.player == null || slotCount <= 0) {
            return 0f;
        }
        int n = Math.min(slotCount, mc.player.inventory.getSizeInventory());
        float best = 0f;
        for (int i = 0; i < n; i++) {
            float h = getBlockHardness(block, mc.player.inventory.getStackInSlot(i), false, false);
            if (h > best) {
                best = h;
            }
        }
        return best;
    }

    public static float getToolDigEfficiency(ItemStack itemStack, Block block, boolean ignoreSlow, boolean ignoreGround) {
        float n = (itemStack == null) ? 1.0f : itemStack.getItem().getStrVsBlock(itemStack, block);
        if (n > 1.0f) {
            final int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (getEnchantmentLevel > 0 && itemStack != null) {
                n += getEnchantmentLevel * getEnchantmentLevel + 1;
            }
        }
        if (mc.player.isPotionActive(Potion.digSpeed)) {
            n *= 1.0f + (mc.player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2f;
        }
        if (!ignoreSlow) {
            if (mc.player.isPotionActive(Potion.digSlowdown)) {
                float n2;
                switch (mc.player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                    case 0: {
                        n2 = 0.3f;
                        break;
                    }
                    case 1: {
                        n2 = 0.09f;
                        break;
                    }
                    case 2: {
                        n2 = 0.0027f;
                        break;
                    }
                    default: {
                        n2 = 8.1E-4f;
                        break;
                    }
                }
                n *= n2;
            }
            if (mc.player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
                n /= 5.0f;
            }
            if (!mc.player.onGround && !ignoreGround) {
                n /= 5.0f;
            }
        }
        return n;
    }

    public static Block getBlock(BlockPos blockPos) {
        return getBlockState(blockPos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block getBlock(Vec3d position) {
        return getBlockState(new BlockPos(position.xCoord, position.yCoord, position.zCoord)).getBlock();
    }

    public static BlockState getBlockState(BlockPos blockPos) {
        if (mc.world == null || blockPos == null) {
            return Blocks.AIR.getDefaultState();
        }
        return mc.world.getBlockState(blockPos);
    }

    public static Box getBlockSelectionBox(BlockPos pos) {
        if (mc.world == null || pos == null) return null;
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();
        block.setBlockBoundsBasedOnState(mc.world, pos);
        Box box = block.getSelectedBoundingBox(mc.world, pos);
        if (box == null) {
            box = new Box(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
        }
        return box;
    }

    public static Box getCollisionOrSelectionBox(BlockPos pos) {
        if (mc.world == null || pos == null) {
            return null;
        }
        BlockState st = mc.world.getBlockState(pos);
        Block block = st.getBlock();
        Box bb = block.getCollisionBoundingBox(mc.world, pos, st);
        if (bb == null) {
            bb = block.getSelectedBoundingBox(mc.world, pos);
        }
        if (bb == null) {
            bb = new Box(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
        }
        return bb;
    }

    public static Box getCollisionOrSelectedOnly(BlockPos pos) {
        if (mc.world == null || pos == null) {
            return null;
        }
        BlockState st = mc.world.getBlockState(pos);
        Block block = st.getBlock();
        Box bb = block.getCollisionBoundingBox(mc.world, pos, st);
        if (bb == null) {
            bb = block.getSelectedBoundingBox(mc.world, pos);
        }
        return bb;
    }

    public static Box unionBlockBounds(BlockPos a, BlockPos b) {
        Box ua = getCollisionOrSelectionBox(a);
        Box ub = getCollisionOrSelectionBox(b);
        return ua.union(ub);
    }

    public static Direction facingFromBlockCenterToPoint(BlockPos pos, Vec3d hit) {
        double px = hit.xCoord - (pos.getX() + 0.5);
        double py = hit.yCoord - (pos.getY() + 0.5);
        double pz = hit.zCoord - (pos.getZ() + 0.5);
        double ax = Math.abs(px);
        double ay = Math.abs(py);
        double az = Math.abs(pz);
        if (ax > ay && ax > az) {
            return px > 0 ? Direction.EAST : Direction.WEST;
        }
        if (ay > az) {
            return py > 0 ? Direction.UP : Direction.DOWN;
        }
        return pz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    public static boolean check(final BlockPos blockPos, final Block block) {
        return getBlock(blockPos) == block;
    }

    public static boolean replaceable(BlockPos blockPos) {
        if (!Utils.nullCheck()) {
            return true;
        }
        return getBlock(blockPos).isReplaceable(mc.world, blockPos);
    }

    public static boolean canSeeVecBlock(final BlockPos pos, final Vec3d vecPlayer, final Vec3d vecBlockPoint) {
        final HitResult mop = mc.world.rayTraceBlocks(vecPlayer, vecBlockPoint, false, false, false);
        if (mop == null) {
            return true;
        }
        if (mop.typeOfHit == HitResult.MovingObjectType.BLOCK) {
            final BlockPos mopPos = mop.getBlockPos();
            if (mopPos.getX() == pos.getX() && mopPos.getY() == pos.getY() && mopPos.getZ() == pos.getZ()) {
                return true;
            }
        }
        return false;
    }

    public static boolean canBlockBeSeen(final BlockPos pos) {
        final Vec3d vecPlayer = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(), mc.player.getZ());
        for (double offsetY = 0.0; offsetY <= 0.5; offsetY += 0.5) {
            final double y = pos.getY() + offsetY;
            Vec3d vecBlockPoint = new Vec3d(pos.getX() + 1, y, pos.getZ() + 0.5);
            if (canSeeVecBlock(pos, vecPlayer, vecBlockPoint)) {
                return true;
            }
            vecBlockPoint = new Vec3d(pos.getX(), y, pos.getZ() + 0.5);
            if (canSeeVecBlock(pos, vecPlayer, vecBlockPoint)) {
                return true;
            }
            vecBlockPoint = new Vec3d(pos.getX() + 0.5, y, (double)(pos.getZ() + 1));
            if (canSeeVecBlock(pos, vecPlayer, vecBlockPoint)) {
                return true;
            }
            vecBlockPoint = new Vec3d(pos.getX() + 0.5, y, (double)pos.getZ());
            if (canSeeVecBlock(pos, vecPlayer, vecBlockPoint)) {
                return true;
            }
        }
        return false;
    }

    public static EnumDyeColor getWoolColor(final BlockState state) {
        return (EnumDyeColor)state.getProperties().get(BlockColored.COLOR);
    }

    public static Direction[] getVisibleFaces(Vec3d eye, BlockPos block) {
        Direction yFace = Math.abs(eye.yCoord - (block.getY() + 1)) < Math.abs(eye.yCoord - block.getY())
                ? Direction.UP : Direction.DOWN;
        Direction zFace = Math.abs(eye.zCoord - (block.getZ() + 1)) < Math.abs(eye.zCoord - block.getZ())
                ? Direction.SOUTH : Direction.NORTH;
        Direction xFace = Math.abs(eye.xCoord - (block.getX() + 1)) < Math.abs(eye.xCoord - block.getX())
                ? Direction.EAST : Direction.WEST;
        return new Direction[]{yFace, zFace, xFace};
    }

    public static boolean containsFace(Direction[] faces, Direction face) {
        for (Direction f : faces) if (f == face) return true;
        return false;
    }

    public static Vec3d getFaceCenter(BlockPos block, Direction face) {
        double eps = 1e-3;
        double cx = block.getX() + 0.5;
        double cy = block.getY() + 0.5;
        double cz = block.getZ() + 0.5;
        switch (face) {
            case UP:    return new Vec3d(cx, block.getY() + 1 - eps, cz);
            case DOWN:  return new Vec3d(cx, block.getY() + eps, cz);
            case NORTH: return new Vec3d(cx, cy, block.getZ() + eps);
            case SOUTH: return new Vec3d(cx, cy, block.getZ() + 1 - eps);
            case EAST:  return new Vec3d(block.getX() + 1 - eps, cy, cz);
            case WEST:  return new Vec3d(block.getX() + eps, cy, cz);
            default:    return new Vec3d(cx, cy, cz);
        }
    }

    public static double dist2PointAABB(Vec3d p, BlockPos b) {
        double cx = Math.max(b.getX(), Math.min(b.getX() + 1, p.xCoord));
        double cy = Math.max(b.getY(), Math.min(b.getY() + 1, p.yCoord));
        double cz = Math.max(b.getZ(), Math.min(b.getZ() + 1, p.zCoord));
        double dx = p.xCoord - cx, dy = p.yCoord - cy, dz = p.zCoord - cz;
        return dx * dx + dy * dy + dz * dz;
    }

    public static boolean canPlaceBlockOnSide(ItemStack stack, BlockPos pos, Direction side) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return false;
        return ((ItemBlock) stack.getItem()).canPlaceBlockOnSide(
                mc.world, pos, side, mc.player, stack);
    }

    public static float getFistBreakTicks(Block block) {
        float hardness = block.getBlockHardness(mc.world, null);
        if (hardness < 0) return Float.MAX_VALUE;
        if (hardness == 0) return 0;
        return hardness * (block.getMaterial().isToolNotRequired() ? 30f : 100f);
    }

    public static boolean hasAirNeighbor(BlockPos pos, BlockPos... exclude) {
        for (Direction f : Direction.values()) {
            BlockPos n = pos.offset(f);
            if (mc.world.getBlockState(n).getBlock() != Blocks.AIR) continue;
            boolean excluded = false;
            for (BlockPos ex : exclude) {
                if (n.equals(ex)) { excluded = true; break; }
            }
            if (!excluded) return true;
        }
        return false;
    }

    public static boolean isAdjacentToBed(BlockPos pos) {
        for (Direction face : Direction.values()) {
            if (getBlock(pos.offset(face)) instanceof BedBlock) return true;
        }
        return false;
    }

    public static HitResult traverseBlocksAlongRay(Vec3d start, Vec3d end,
            boolean wantBed, boolean wantAdjacent) {
        if (mc.world == null) return null;
        if (Double.isNaN(start.xCoord) || Double.isNaN(start.yCoord) || Double.isNaN(start.zCoord)) return null;
        if (Double.isNaN(end.xCoord) || Double.isNaN(end.yCoord) || Double.isNaN(end.zCoord)) return null;

        int destX = MathHelper.floor_double(end.xCoord);
        int destY = MathHelper.floor_double(end.yCoord);
        int destZ = MathHelper.floor_double(end.zCoord);
        int curX = MathHelper.floor_double(start.xCoord);
        int curY = MathHelper.floor_double(start.yCoord);
        int curZ = MathHelper.floor_double(start.zCoord);

        HitResult firstHit = null;

        HitResult candidate = getBlockCollisionHit(curX, curY, curZ, start, end);
        if (candidate != null) {
            if (isBedOrAdjacentMatch(candidate.getBlockPos(), wantBed, wantAdjacent)) return candidate;
            firstHit = candidate;
        }

        Vec3d tracePos = start;
        int remaining = 200;

        while (remaining-- >= 0) {
            if (Double.isNaN(tracePos.xCoord) || Double.isNaN(tracePos.yCoord) || Double.isNaN(tracePos.zCoord))
                return firstHit;
            if (curX == destX && curY == destY && curZ == destZ)
                return firstHit;

            boolean crossX = true, crossY = true, crossZ = true;
            double boundX = 999.0, boundY = 999.0, boundZ = 999.0;
            if (destX > curX) boundX = (double) curX + 1.0;
            else if (destX < curX) boundX = (double) curX;
            else crossX = false;
            if (destY > curY) boundY = (double) curY + 1.0;
            else if (destY < curY) boundY = (double) curY;
            else crossY = false;
            if (destZ > curZ) boundZ = (double) curZ + 1.0;
            else if (destZ < curZ) boundZ = (double) curZ;
            else crossZ = false;

            double dx = end.xCoord - tracePos.xCoord;
            double dy = end.yCoord - tracePos.yCoord;
            double dz = end.zCoord - tracePos.zCoord;
            double tX = 999.0, tY = 999.0, tZ = 999.0;
            if (crossX) tX = (boundX - tracePos.xCoord) / dx;
            if (crossY) tY = (boundY - tracePos.yCoord) / dy;
            if (crossZ) tZ = (boundZ - tracePos.zCoord) / dz;
            if (tX == -0.0) tX = -1.0E-4;
            if (tY == -0.0) tY = -1.0E-4;
            if (tZ == -0.0) tZ = -1.0E-4;

            Direction face;
            if (tX < tY && tX < tZ) {
                face = destX > curX ? Direction.WEST : Direction.EAST;
                tracePos = new Vec3d(boundX, tracePos.yCoord + dy * tX, tracePos.zCoord + dz * tX);
            } else if (tY < tZ) {
                face = destY > curY ? Direction.DOWN : Direction.UP;
                tracePos = new Vec3d(tracePos.xCoord + dx * tY, boundY, tracePos.zCoord + dz * tY);
            } else {
                face = destZ > curZ ? Direction.NORTH : Direction.SOUTH;
                tracePos = new Vec3d(tracePos.xCoord + dx * tZ, tracePos.yCoord + dy * tZ, boundZ);
            }

            curX = MathHelper.floor_double(tracePos.xCoord) - (face == Direction.EAST ? 1 : 0);
            curY = MathHelper.floor_double(tracePos.yCoord) - (face == Direction.UP ? 1 : 0);
            curZ = MathHelper.floor_double(tracePos.zCoord) - (face == Direction.SOUTH ? 1 : 0);

            candidate = getBlockCollisionHit(curX, curY, curZ, start, end);
            if (candidate != null) {
                if (isBedOrAdjacentMatch(candidate.getBlockPos(), wantBed, wantAdjacent)) return candidate;
                if (firstHit == null) firstHit = candidate;
            }
        }
        return firstHit;
    }

    private static HitResult getBlockCollisionHit(int x, int y, int z, Vec3d start, Vec3d end) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.canCollideCheck(state, false)) return null;
        return block.collisionRayTrace(mc.world, pos, start, end);
    }

    private static boolean isBedOrAdjacentMatch(BlockPos pos, boolean wantBed, boolean wantAdjacent) {
        Block block = getBlock(pos);
        boolean isBed = block instanceof BedBlock;
        if (wantBed && isBed) return true;
        if (wantAdjacent && !isBed && isAdjacentToBed(pos)) return true;
        return false;
    }
}