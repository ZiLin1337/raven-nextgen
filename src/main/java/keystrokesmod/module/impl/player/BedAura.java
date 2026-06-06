package keystrokesmod.module.impl.player;

import keystrokesmod.event.ClientRotationEvent;
import keystrokesmod.event.PreAttackEvent;
import keystrokesmod.event.PrePlayerInteractEvent;
import keystrokesmod.event.PreSlotScrollEvent;
import keystrokesmod.event.SlotUpdateEvent;
// import keystrokesmod.mixin.impl.accessor.IAccessorEntityRenderer;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.impl.render.BlockOverlay;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.GroupSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.block.Blocks;
import net.minecraft.util.*;


import java.util.*;

public class BedAura extends Module {

    private final SliderSetting fov;
    private final SliderSetting range;
    private final SliderSetting rate;
    private final SliderSetting breakDelay;
    private final SliderSetting breakSpeed;
    private final ButtonSetting whitelistOwnBed;
    private final ButtonSetting prioritizeKillAura;
    private final GroupSetting swapGroup;
    private final ButtonSetting switchBackWhenDone;
    private final ButtonSetting overrideSwapBack;
    private final ButtonSetting renderOutline;
    private final ColorSetting outlineColor;

    private static final int MS_PER_TICK = 50;
    private static final double BED_FIND_EXTRA_BLOCKS = 1.0;
    private static final double OWN_BED_PROTECTION_RADIUS_SQ = 800.0;
    private final List<BlockPos[]> bedPairsCache = new ArrayList<>();
    private int scanCooldown;

    private BlockPos targetPos;
    private Vec3d targetHitVec;
    private Direction targetSide;

    private boolean miningActive;
    private int hotbarProgrammaticDepth;
    private boolean hasSwapped;
    private int previousSlot = -1;
    private BlockPos spawnAnchor;
    private boolean pendingSpawnAnchorCapture;
    private boolean waitingForRespawn;
    private long respawnMessageTime;

    public BedAura() {
        super("Bed Aura", category.player);
        this.registerSetting(breakSpeed = new SliderSetting("Break speed", "x", 1.0, 1.0, 2.0, 0.02));
        this.registerSetting(breakDelay = new SliderSetting("Break delay", "ms", 250.0, 0.0, 250.0, 50.0));
        this.registerSetting(range = new SliderSetting("Range", " blocks", 4.5, 2.0, 6.0, 0.1));
        this.registerSetting(fov = new SliderSetting("FOV", "", 180.0, 30.0, 360.0, 1.0));
        this.registerSetting(rate = new SliderSetting("Scan rate", "ms", 250.0, 50.0, 2000.0, 50.0));
        this.registerSetting(whitelistOwnBed = new ButtonSetting("Whitelist own bed", true));
        this.registerSetting(prioritizeKillAura = new ButtonSetting("Prioritize KillAura", false));
        this.registerSetting(swapGroup = new GroupSetting("Swap"));
        this.registerSetting(switchBackWhenDone = new ButtonSetting(swapGroup, "Switch back when done", true, "Swap to previous slot"));
        this.registerSetting(overrideSwapBack = new ButtonSetting(swapGroup, "Override swap back", true));
        this.registerSetting(renderOutline = new ButtonSetting("Render block outline", true));
        this.registerSetting(outlineColor = new ColorSetting("Outline color", 255, 64, 64, 229));
    }

    @Override
    public void guiUpdate() {
        outlineColor.setVisible(renderOutline.isToggled(), this);
    }

    @Override
    public void onDisable() {
        resetMining();
        resetSpawnTracking();
        bedPairsCache.clear();
        scanCooldown = 0;
    }

    @Override
    public void onUpdate() {
        if (!Utils.nullCheck()) {
            return;
        }

        if (pendingSpawnAnchorCapture && Utils.getBedwarsStatus() == 2) {
            spawnAnchor = mc.player.getPosition();
            pendingSpawnAnchorCapture = false;
        }
    }

    
    public void onWorldJoin(/* EntityJoinWorldEvent */ e) {
        if (e.entity == mc.player) {
            resetSpawnTracking();
        }
    }

    
    public void onChat(/* ClientChatReceivedEvent */ event) {
        if (!Utils.nullCheck()) {
            return;
        }

        String strippedMessage = Utils.stripColor(event.message.getUnformattedText());
        if (strippedMessage.startsWith(" ") && strippedMessage.contains("Protect your bed and destroy the enemy beds.")) {
            pendingSpawnAnchorCapture = true;
            waitingForRespawn = false;
        }
        else if (strippedMessage.equals("You will respawn because you still have a bed!")) {
            waitingForRespawn = true;
            respawnMessageTime = System.currentTimeMillis();
        }
        else if (strippedMessage.equals("You have respawned!") && waitingForRespawn && Utils.timeBetween(System.currentTimeMillis(), respawnMessageTime) <= 12000) {
            pendingSpawnAnchorCapture = true;
            waitingForRespawn = false;
        }
    }public void onPrePlayerInteract(PrePlayerInteractEvent e) {
        applyMiningKeyState();
    }public void onMouse(MouseEvent e) {
        if (!shouldSuppressManualMouse()) {
            return;
        }
        if (e.button == 0 || e.button == 1) {
            e.setCanceled(true);
        }
    }public void onPreAttack(PreAttackEvent e) {
        if (!shouldSuppressManualMouse()) {
            return;
        }
        e.setCanceled(true);
    }public void onSlotScroll(PreSlotScrollEvent e) {
        if (!shouldSuppressManualMouse()) {
            return;
        }
        if (hasSwapped && overrideSwapBack.isToggled() && Utils.nullCheck()) {
            int slot = Integer.compare(e.slot, 0);
            previousSlot = Math.floorMod(mc.player.inventory.currentItem - slot, PlayerInventory.getHotbarSize());
        }
        e.setCanceled(true);
    }public void onSlotUpdate(SlotUpdateEvent e) {
        if (!shouldSuppressManualMouse() || hotbarProgrammaticDepth > 0) {
            return;
        }
        if (hasSwapped && overrideSwapBack.isToggled()) {
            previousSlot = e.slot;
        }
        e.setCanceled(true);
    }

    private boolean shouldSuppressManualMouse() {
        return miningActive && isEnabled() && Utils.nullCheck() && mc.currentScreen == null && canMineBlocks() && !shouldYieldToKillAura();
    }

    public void applyMiningKeyState() {
        if (!canMineBlocks() || shouldYieldToKillAura()) {
            if (miningActive) {
                resetMining();
            }
            return;
        }
        if (!miningActive || !isEnabled() || !Utils.nullCheck() || mc.currentScreen != null) {
            return;
        }
        int atk = mc.gameSettings.keyBindAttack.getKeyCode();
        int use = mc.gameSettings.keyBindUseItem.getKeyCode();
        InputUtil.setKeyPressed(atk, false);
        InputUtil.setKeyPressed(use, false);
        InputUtil.setKeyPressed(atk, true);
    }

    public BlockPos getAuraTargetPos() {
        return miningActive && canMineBlocks() ? targetPos : null;
    }

    public boolean isActivelyMining() {
        return miningActive && isEnabled() && Utils.nullCheck() && mc.currentScreen == null && canMineBlocks() && !shouldYieldToKillAura();
    }

    public boolean shouldOverrideFastMine() {
        return isActivelyMining();
    }

    public float getBreakSpeedMultiplier() {
        float multiplier = (float) breakSpeed.getInput();
        return multiplier > 1.0f ? multiplier : 1.0f;
    }

    public int getBreakDelayTicks() {
        return Math.max(0, Math.min(5, (int) (breakDelay.getInput() / 50.0)));
    }

    public float getAuraBreakProgress() {
        if (!canMineBlocks() || !miningActive || mc.playerController == null) {
            return 0f;
        }
        IAccessorPlayerControllerMP pc = (IAccessorPlayerControllerMP) mc.playerController;
        BlockPos currentBlock = pc.getCurrentBlock();
        if (targetPos == null || currentBlock == null || !targetPos.equals(currentBlock)) {
            return 0f;
        }
        return pc.getCurBlockDamageMP();
    }

    public boolean shouldOverrideMouseOver() {
        return isEnabled() && miningActive && canMineBlocks() && targetPos != null && targetHitVec != null && targetSide != null && Utils.nullCheck() && !shouldYieldToKillAura();
    }

    public void modifyMouseOverFromGetMouseOver(float partialTicks) {
        if (!shouldOverrideMouseOver()) {
            return;
        }
        if (mc.getRenderViewEntity() == null) {
            return;
        }

        HitResult mop = new MovingObjectPosition(targetHitVec, targetSide, targetPos);
        mc.objectMouseOver = mop;
        mc.pointedEntity = null;

        EntityRenderer renderer = mc.entityRenderer;
        if (renderer instanceof IAccessorEntityRenderer) {
            ((IAccessorEntityRenderer) renderer).setPointedEntity(null);
        }
    }public void onClientRotation(ClientRotationEvent e) {
        if (!isEnabled() || !Utils.nullCheck() || mc.currentScreen != null || !canMineBlocks()) {
            resetMining();
            return;
        }
        if (shouldYieldToKillAura()) {
            resetMining();
            return;
        }
        if (e.scriptRotations) {
            resetMining();
            return;
        }

        double reach = range.getInput();
        double reachSq = reach * reach;

        if (--scanCooldown <= 0) {
            scanCooldown = Math.max(1, (int) Math.round(rate.getInput() / (double) MS_PER_TICK));
            rebuildBedPairsCache(reach + BED_FIND_EXTRA_BLOCKS);
        }

        if (bedPairsCache.isEmpty()) {
            resetMining();
            return;
        }

        Choice best = chooseBestTarget(reachSq);
        if (best == null) {
            resetMining();
            return;
        }

        targetPos = best.pos;
        targetHitVec = best.hitVec;
        targetSide = best.side;
        miningActive = true;

        equipBestHotbarTool(BlockUtils.getBlock(targetPos));

        float baseYaw = e.yaw != null ? e.yaw : RotationUtils.serverRotations[0];
        float basePitch = e.pitch != null ? e.pitch : RotationUtils.serverRotations[1];
        float[] r = RotationUtils.getRotationsToPoint(
                targetHitVec.xCoord, targetHitVec.yCoord, targetHitVec.zCoord,
                baseYaw, basePitch
        );
        e.setYaw(r[0]);
        e.setPitch(r[1]);
    }

    
    public void onRenderWorldLast(/* RenderWorldLastEvent */ e) {
        if (!isEnabled() || !renderOutline.isToggled() || !miningActive || targetPos == null || !Utils.nullCheck() || !canMineBlocks()) {
            return;
        }
        BlockState st = mc.world.getBlockState(targetPos);
        Block b = st.getBlock();
        if (b == null || b == Blocks.AIR) {
            return;
        }
        int c = outlineColor.getColor();
        BlockOverlay.renderBlockOutline(targetPos, c, c, 2.0f, true);
    }

    private void resetMining() {
        miningActive = false;
        if (switchBackWhenDone.isToggled() && previousSlot != -1 && Utils.nullCheck()) {
            setSlot(previousSlot);
        }
        InputUtil.setKeyPressed(mc.gameSettings.keyBindAttack.getKeyCode(), GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
        InputUtil.setKeyPressed(mc.gameSettings.keyBindUseItem.getKeyCode(), GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS);
        hotbarProgrammaticDepth = 0;
        targetPos = null;
        targetHitVec = null;
        targetSide = null;
        hasSwapped = false;
        previousSlot = -1;
    }

    private void rebuildBedPairsCache(double searchRange) {
        bedPairsCache.clear();
        Set<BlockPos> seenFeet = new HashSet<>();
        int ri = (int) Math.ceil(searchRange);
        BlockPos origin = new BlockPos(mc.player);

        for (int dx = -ri; dx <= ri; dx++) {
            for (int dy = -ri; dy <= ri; dy++) {
                for (int dz = -ri; dz <= ri; dz++) {
                    BlockPos p = origin.add(dx, dy, dz);
                    BlockPos[] pair = footHeadPair(p);
                    if (pair == null) {
                        continue;
                    }
                    BlockPos foot = pair[0];
                    if (seenFeet.contains(foot)) {
                        continue;
                    }
                    if (!bedInSearchRange(pair, searchRange)) {
                        continue;
                    }
                    Vec3d center = bedCenter(pair);
                    if (!inFov(center, (float) fov.getInput())) {
                        continue;
                    }
                    seenFeet.add(foot);
                    bedPairsCache.add(pair);
                }
            }
        }

        removeOwnBedPair();
    }

    private BlockPos[] footHeadPair(BlockPos at) {
        BlockState st = mc.world.getBlockState(at);
        if (!(st.getBlock() instanceof BedBlock)) {
            return null;
        }
        BedBlock.EnumPartType part = (BedBlock.EnumPartType) st.getValue(BedBlock.PART);
        Direction facing = (Direction) st.getValue(BedBlock.FACING);
        BlockPos foot = part == BedBlock.EnumPartType.FOOT ? at : at.offset(facing.getOpposite());
        BlockState footSt = mc.world.getBlockState(foot);
        if (!(footSt.getBlock() instanceof BedBlock)) {
            return null;
        }
        if (footSt.getValue(BedBlock.PART) != BedBlock.EnumPartType.FOOT) {
            return null;
        }
        Direction footFacing = (Direction) footSt.getValue(BedBlock.FACING);
        BlockPos head = foot.offset(footFacing);
        BlockState hs = mc.world.getBlockState(head);
        if (!(hs.getBlock() instanceof BedBlock)) {
            return null;
        }
        if (hs.getValue(BedBlock.PART) != BedBlock.EnumPartType.HEAD) {
            return null;
        }
        if (hs.getValue(BedBlock.FACING) != footFacing) {
            return null;
        }
        return new BlockPos[]{foot, head};
    }

    private Vec3d bedCenter(BlockPos[] pair) {
        Box a = BlockUtils.unionBlockBounds(pair[0], pair[1]);
        return new Vec3d((a.minX + a.maxX) * 0.5, (a.minY + a.maxY) * 0.5, (a.minZ + a.maxZ) * 0.5);
    }

    private boolean bedInSearchRange(BlockPos[] pair, double searchRadius) {
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        double r2 = searchRadius * searchRadius + 1e-4;
        Box u = BlockUtils.unionBlockBounds(pair[0], pair[1]);
        Vec3d onBox = RotationUtils.closestPointOnAabb(u, eye);
        if (eye.squareDistanceTo(onBox) <= r2) {
            return true;
        }
        Vec3d mid = new Vec3d((u.minX + u.maxX) * 0.5, (u.minY + u.maxY) * 0.5, (u.minZ + u.maxZ) * 0.5);
        return eye.squareDistanceTo(mid) <= r2;
    }

    private boolean inFov(Vec3d worldPoint, float fovDeg) {
        if (fovDeg >= 360) {
            return true;
        }
        Vec3d eyes = mc.player.getPositionEyes(1f);
        Vec3d look = mc.player.getLook(1f);
        Vec3d to = worldPoint.subtract(eyes);
        double len = to.lengthVector();
        if (len < 1e-6) {
            return true;
        }
        to = new Vec3d(to.xCoord / len, to.yCoord / len, to.zCoord / len);
        double dot = look.xCoord * to.xCoord + look.yCoord * to.yCoord + look.zCoord * to.zCoord;
        double ang = Math.acos(MathHelper.clamp_double(dot, -1.0, 1.0)) * (180.0 / Math.PI);
        return ang <= fovDeg * 0.5;
    }

    private Choice chooseBestTarget(double reachSq) {
        IAccessorPlayerControllerMP pc = (IAccessorPlayerControllerMP) mc.playerController;
        float curProg = pc.getCurBlockDamageMP();
        BlockPos breaking = pc.getCurrentBlock();

        List<BlockPos[]> exposed = new ArrayList<>();
        List<BlockPos[]> covered = new ArrayList<>();
        for (BlockPos[] pair : bedPairsCache) {
            if (isBedExposed(pair)) {
                exposed.add(pair);
            } else {
                covered.add(pair);
            }
        }
        sortBedsByEyeDistance(exposed);
        sortBedsByEyeDistance(covered);

        Choice c = pickBestOnClosestBedWithCandidates(exposed, reachSq, curProg, breaking);
        if (c != null) {
            return c;
        }
        return pickBestOnClosestBedWithCandidates(covered, reachSq, curProg, breaking);
    }

    private void sortBedsByEyeDistance(List<BlockPos[]> pairs) {
        Vec3d eye = mc.player.getPositionEyes(1f);
        pairs.sort(Comparator.comparingDouble(p -> eye.squareDistanceTo(bedCenter(p))));
    }

    private Choice pickBestOnClosestBedWithCandidates(List<BlockPos[]> sortedPairs, double reachSq, float curProg, BlockPos breaking) {
        for (BlockPos[] pair : sortedPairs) {
            List<Choice> candidates = buildCandidates(pair, reachSq);
            if (candidates.isEmpty()) {
                continue;
            }
            Choice best = null;
            double bestScore = Double.POSITIVE_INFINITY;
            for (Choice ch : candidates) {
                double score = scoreChoice(ch, curProg, breaking);
                if (score < bestScore) {
                    bestScore = score;
                    best = ch;
                }
            }
            return best;
        }
        return null;
    }

    private double scoreChoice(Choice ch, float curProg, BlockPos breaking) {
        Block block = BlockUtils.getBlock(ch.pos);
        float bestHotbar = BlockUtils.maxDigRateAcrossSlots(block, PlayerInventory.getHotbarSize());
        if (bestHotbar <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        double timeEst = 1.0 / bestHotbar;

        if (breaking != null && breaking.equals(ch.pos) && curProg > 0.02f) {
            timeEst -= curProg * 12.0;
        }
        Vec3d eye = mc.player.getPositionEyes(1f);
        timeEst += eye.squareDistanceTo(ch.hitVec) * 0.002;
        return timeEst;
    }

    private List<Choice> buildCandidates(BlockPos[] pair, double reachSq) {
        List<Choice> out = new ArrayList<>();
        boolean exposed = isBedExposed(pair);

        if (exposed) {
            for (BlockPos bp : pair) {
                addBlockCandidate(bp, reachSq, out);
            }
        } else {
            Set<BlockPos> seen = new HashSet<>();
            for (BlockPos bp : pair) {
                for (Direction f : Direction.values()) {
                    if (f == Direction.DOWN) {
                        continue;
                    }
                    BlockPos n = bp.offset(f);
                    if (seen.contains(n)) {
                        continue;
                    }
                    BlockState st = mc.world.getBlockState(n);
                    Block b = st.getBlock();
                    if (b == Blocks.AIR || b instanceof BedBlock) {
                        continue;
                    }
                    float hard = b.getBlockHardness(mc.world, n);
                    if (hard < 0) {
                        continue;
                    }
                    seen.add(n);
                    addBlockCandidate(n, reachSq, out);
                }
            }
        }
        return out;
    }

    private boolean isBedExposed(BlockPos[] pair) {
        for (BlockPos bp : pair) {
            for (Direction f : Direction.values()) {
                BlockPos n = bp.offset(f);
                if (mc.world.getBlockState(n).getBlock() == Blocks.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addBlockCandidate(BlockPos pos, double reachSq, List<Choice> out) {
        BlockState st = mc.world.getBlockState(pos);
        Block block = st.getBlock();
        if (block == Blocks.AIR) {
            return;
        }
        float hard = block.getBlockHardness(mc.world, pos);
        if (hard < 0) {
            return;
        }
        Box bb = BlockUtils.getBlockSelectionBox(pos);
        if (bb == null) {
            return;
        }
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        Vec3d hit = RotationUtils.closestPointOnAabb(bb, eye);
        if (eye.squareDistanceTo(hit) > reachSq + 1e-3) {
            return;
        }

        HitResult trace = block.collisionRayTrace(mc.world, pos, eye, hit.addVector(
                (hit.xCoord - eye.xCoord) * 0.01,
                (hit.yCoord - eye.yCoord) * 0.01,
                (hit.zCoord - eye.zCoord) * 0.01
        ));
        Direction side = BlockUtils.facingFromBlockCenterToPoint(pos, hit);
        if (trace != null && trace.hitVec != null && trace.sideHit != null && pos.equals(trace.getBlockPos())) {
            hit = trace.hitVec;
            side = trace.sideHit;
        }
        if (block instanceof BedBlock && side == Direction.DOWN) {
            return;
        }

        out.add(new Choice(pos, hit, side));
    }

    private void equipBestHotbarTool(Block block) {
        int slot = Utils.getTool(block);
        if (slot < 0) {
            return;
        }
        if (previousSlot == -1 && slot != mc.player.inventory.currentItem) {
            previousSlot = mc.player.inventory.currentItem;
        }
        if (slot != mc.player.inventory.currentItem) {
            setSlot(slot);
        }
    }

    private void setSlot(int slot) {
        if (slot == -1 || slot == mc.player.inventory.currentItem) {
            return;
        }
        hotbarProgrammaticDepth++;
        try {
            mc.player.inventory.currentItem = slot;
            hasSwapped = true;
            ((IAccessorPlayerControllerMP) mc.playerController).callSyncCurrentPlayItem();
        } finally {
            hotbarProgrammaticDepth--;
        }
    }

    private boolean canMineBlocks() {
        return mc.player.capabilities.allowEdit
                && !mc.player.capabilities.isCreativeMode
                && !mc.player.isSpectator();
    }

    private boolean shouldYieldToKillAura() {
        if (!prioritizeKillAura.isToggled()) {
            return false;
        }
        return ModuleManager.killAura != null
                && ModuleManager.killAura.isEnabled()
                && KillAura.target != null;
    }

    private void resetSpawnTracking() {
        spawnAnchor = null;
        pendingSpawnAnchorCapture = false;
        waitingForRespawn = false;
        respawnMessageTime = 0L;
    }

    private void removeOwnBedPair() {
        if (!shouldWhitelistOwnBed() || bedPairsCache.isEmpty()) {
            return;
        }

        BlockPos[] ownBedPair = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        Vec3d spawnCenter = spawnAnchorCenter();

        for (BlockPos[] pair : bedPairsCache) {
            double distance = spawnCenter.squareDistanceTo(bedCenter(pair));
            if (distance < closestDistance) {
                closestDistance = distance;
                ownBedPair = pair;
            }
        }

        if (ownBedPair != null) {
            bedPairsCache.remove(ownBedPair);
        }
    }

    private boolean shouldWhitelistOwnBed() {
        return whitelistOwnBed.isToggled()
                && spawnAnchor != null
                && Utils.getBedwarsStatus() == 2
                && mc.player.getDistanceSq(spawnAnchor) <= OWN_BED_PROTECTION_RADIUS_SQ;
    }

    private Vec3d spawnAnchorCenter() {
        return new Vec3d(spawnAnchor.getX() + 0.5, spawnAnchor.getY() + 0.5, spawnAnchor.getZ() + 0.5);
    }

    private static final class Choice {
        final BlockPos pos;
        final Vec3d hitVec;
        final Direction side;

        Choice(BlockPos pos, Vec3d hitVec, Direction side) {
            this.pos = pos;
            this.hitVec = hitVec;
            this.side = side;
        }
    }
}
