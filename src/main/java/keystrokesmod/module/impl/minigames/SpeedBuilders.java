package keystrokesmod.module.impl.minigames;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import keystrokesmod.event.TickEvent;
import net.minecraft.util.hit.HitResult;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.mixin.impl.accessor.IAccessorMinecraft;
import keystrokesmod.module.Module;
import keystrokesmod.event.MouseEvent;
import keystrokesmod.event.RenderWorldLastEvent;
import keystrokesmod.event.EntityJoinWorldEvent;
import keystrokesmod.event.ClientChatReceivedEvent;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.*;
// Removed Forge event
// Removed Forge event

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpeedBuilders extends Module {

    private SliderSetting placeDelay;
    private ButtonSetting antiMiss;
    private ButtonSetting autoPlace;
    private ButtonSetting autoSwap;
    private ButtonSetting hoverPlace;
    private ButtonSetting infoHud;
    private ButtonSetting renderBlocks;
    private ButtonSetting renderOnlyPlaceable;

    private ConcurrentHashMap<BlockPos, BuildBlockInfo> buildInfo = new ConcurrentHashMap<>();

    private BlockPos platformCenter;
    private boolean listenForPacket;

    public List<BlockPos> PLATFORM_POSITIONS = Arrays.asList(
            new BlockPos(45, 71, -18),
            new BlockPos(-16, 71, 45),
            new BlockPos(18, 71, 45),
            new BlockPos(45, 71, 16),
            new BlockPos(-18, 71, -45),
            new BlockPos(-45, 71, -16),
            new BlockPos(-45, 71, 18),
            new BlockPos(16, 71, -45)
    );

    private int HIGHLIGHT_COLOR = new Color(31, 255, 22, 44).getRGB();
    private int UNPLACEABLE_COLOR = new Color(184, 255, 183, 30).getRGB();

    private boolean doneCollecting;
    private double blockCount;
    private int lastPlaceTick = 0;
    private boolean eliminated;

    public SpeedBuilders() {
        super("Speed Builders", category.minigames);
        this.registerSetting(new DescriptionSetting("Middle click to toggle auto."));
        this.registerSetting(placeDelay = new SliderSetting("Place delay", " tick", 0.5, 0, 10, 0.5));
        this.registerSetting(antiMiss = new ButtonSetting("Anti miss", false));
        this.registerSetting(autoPlace = new ButtonSetting("Auto place", false));
        this.registerSetting(autoSwap = new ButtonSetting("Auto swap", true));
        this.registerSetting(hoverPlace = new ButtonSetting("Hover place", true));
        this.registerSetting(infoHud = new ButtonSetting("Info HUD", true));
        this.registerSetting(renderBlocks = new ButtonSetting("Render blocks", true));
        this.registerSetting(renderOnlyPlaceable = new ButtonSetting("Render only placeable", false));
    }

    @Override
    public void onDisable() {
        lastPlaceTick = 0;
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        int gameStatus = getGameStatus();
        if (gameStatus == -1 || platformCenter == null) {
            return;
        }
        if (gameStatus == 4) {
            doneCollecting = true;
        }
        if (gameStatus == 1 && !doneCollecting) {
            buildInfo = getBuildInfo(platformCenter);
            if (!buildInfo.isEmpty()) {
                blockCount = buildInfo.size();
            }
        }
        if (gameStatus == 2) {
            doneCollecting = false;
            for (Map.Entry<BlockPos, BuildBlockInfo> entry : buildInfo.entrySet()) {
                BlockState currentState = mc.world.getBlockState(entry.getKey());
                BlockState requiredState = entry.getValue().requiredState;

                if (currentState == null || requiredState == null) {
                    entry.getValue().isPlaced = false;
                    continue;
                }

                if (currentState.equals(requiredState) ||(requiredState.getBlock() instanceof BlockLeaves && (currentState.getBlock().equals(requiredState.getBlock())))) {
                    entry.getValue().isPlaced = true;
                }
                else {
                    entry.getValue().isPlaced = false;
                }
            }

            if (getLookInfo() != null) {
                HitResult mop = getLookInfo();
                if (mop.sideHit != null) {
                    BlockPos targetPos = mop.getBlockPos();
                    BlockPos facePos = targetPos.offset(mop.sideHit);

                    BuildBlockInfo info = buildInfo.get(facePos);
                    if (info != null && !info.isPlaced) {
                        if (autoSwap.isToggled()) {
                            int requiredMeta = info.requiredState.getBlock().getMetaFromState(info.requiredState);
                            int slot = getSlot(info.requiredState.getBlock(), requiredMeta);
                            if (slot != -1 && slot != mc.player.inventory.currentItem) {
                                mc.player.inventory.currentItem = slot;
                            }
                        }
                        if ((hoverPlace.isToggled()) && holdingSameBlock(info.requiredState) && correctPlaceState(info.requiredState, targetPos, mop.sideHit, mop.hitVec, mc.player.getHeldItem())) {
                            if (lastPlaceTick++ < placeDelay.getInput()) {
                                return;
                            }
                            ((IAccessorMinecraft) mc).callRightClickMouse();
                            lastPlaceTick = 0;
                        }
                    }
                }
            }
        }
    }

    
    public void onMouse(MouseEvent e) {
        if (!e.buttonstate || !Utils.nullCheck() || mc.currentScreen != null) {
            return;
        }
        if (e.button == 1 && antiMiss.isToggled() && getLookInfo() != null && getGameStatus() == 2) {
            HitResult mop = getLookInfo();
            if (mop.sideHit != null) {
                BlockPos targetPos = mop.getBlockPos();
                BlockPos facePos = targetPos.offset(mop.sideHit);

                BuildBlockInfo info = buildInfo.get(facePos);
                if (info == null || !holdingSameBlock(info.requiredState) || !correctPlaceState(info.requiredState, targetPos, mop.sideHit, mop.hitVec, mc.player.getHeldItem())) {
                    e.setCanceled(true);
                }
            }
        }
        else if (e.button == 2) {
            if (autoSwap.isToggled()) {
                autoSwap.disable();
                hoverPlace.disable();
            }
            else {
                autoSwap.enable();
                hoverPlace.enable();
            }
        }
    }

    
    public void onRenderWorld(RenderWorldLastEvent ev) {
        if (!Utils.nullCheck() || getGameStatus() != 2 || !renderBlocks.isToggled()) {
            return;
        }

        for (Map.Entry<BlockPos, BuildBlockInfo> buildData : buildInfo.entrySet()) {
            BuildBlockInfo info = buildData.getValue();
            if (info.isPlaced) {
                continue;
            }
            if (!holdingSameBlock(info.requiredState)) {
                continue;
            }
            BlockPos pos = buildData.getKey();
            boolean useWhite = true;
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.offset(dir);
                if (BlockUtils.getBlockState(neighborPos) != Blocks.AIR) {
                    useWhite = false;
                }
            }
            if (renderOnlyPlaceable.isToggled() && useWhite) {
                continue;
            }
            RenderUtils.renderBlockModel(buildData.getValue().requiredState, pos.getX(), pos.getY(), pos.getZ(), useWhite ? UNPLACEABLE_COLOR : HIGHLIGHT_COLOR);
        }
    }

    
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !Utils.nullCheck() || mc.currentScreen != null) {
            return;
        }
        int gameStatus = getGameStatus();
        if (infoHud.isToggled()) {
            List<String> lines = new ArrayList<>();
            lines.add("§6Speed Builders");
            lines.add("§7Status: §b" + ((gameStatus == 1 || gameStatus == 4)
                    ? "Showing" : (gameStatus == 2)
                    ? "Building" : (gameStatus == 3)
                    ? "Judging" : "§cDisabled"));
            if (gameStatus == 2 && !eliminated) {
                double placedCount = 0;
                for (BuildBlockInfo info : buildInfo.values()) {
                    if (info.isPlaced) placedCount++;
                }
                double percentage = 0.0;
                if (buildInfo.isEmpty()) {
                    percentage = 100.0;
                    placedCount = blockCount;
                } else if (blockCount > 0) {
                    percentage = ((placedCount) / blockCount) * 100.0;
                }
                lines.add("§7Progress: §b" + (int) placedCount + "§7/§b" + (int) blockCount + " " + Math.round(percentage) + "%");
            }
            lines.add("§7Auto: " + (autoEnabled() ? "§aENABLED" : "§cDISABLED"));

            int padding = 4;
            int maxWidth = 0;
            for (String line : lines) {
                int lineWidth = mc.textRenderer.getStringWidth(line);
                if (lineWidth > maxWidth) {
                    maxWidth = lineWidth;
                }
            }

            int lineHeight = mc.textRenderer.FONT_HEIGHT;
            int lineSpacing = 3;
            int totalHeight = lines.size() * lineHeight + (lines.size() - 1) * lineSpacing + padding * 2;
            int totalWidth = maxWidth + padding * 2;

            float x = -5;
            float y = 110;

            RenderUtils.drawRoundedRectangle(x, y, x + totalWidth + 7, y + totalHeight - 2, 7, Utils.mergeAlpha(Color.black.getRGB(), 120));

            float textX = x + padding;
            float textY = y + padding;

            for (int i = 0; i < lines.size(); i++) {
                mc.textRenderer.drawString(lines.get(i), (int) (textX + 5), (int) (textY + i * (lineHeight + lineSpacing)), -1);
            }
        }
    }

    
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (!Utils.nullCheck() || e.entity == null) {
            return;
        }
        if (e.entity == mc.player) {
            buildInfo.clear();
            platformCenter = null;
            listenForPacket = false;
            doneCollecting = false;
            eliminated = false;
        }
    }

    
    public void onChat(ClientChatReceivedEvent e) {
        if (e.type == 2 || !Utils.nullCheck() || getGameStatus() == -1 || listenForPacket) {
            return;
        }
        String stripped = Utils.stripColor(e.message.getUnformattedText());
        if (stripped.isEmpty()) {
            return;
        }
        if (stripped.contains("Perfectly recreate the build you are shown each") || stripped.contains("The game starts in 1 second!")) {
            listenForPacket = true;
        }
        if (stripped.startsWith(Utils.getServerName()) && stripped.contains(" got a perfect build in ") && stripped.endsWith("s!")) {
            buildInfo.clear();
            doneCollecting = false;
        }
        if (stripped.startsWith("Player eliminated: " + Utils.getServerName()) && stripped.endsWith("%)")) {
            eliminated = true;
        }
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (listenForPacket && Utils.nullCheck() && e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            Vec3d setPos = new Vec3d(((PlayerPositionLookS2CPacket) e.getPacket()).getX(), ((PlayerPositionLookS2CPacket) e.getPacket()).getY(), ((PlayerPositionLookS2CPacket) e.getPacket()).getZ());
            if (platformCenter == null) {
                platformCenter = findCenter(setPos);
            }
            listenForPacket = false;
        }
    }

    public int getGameStatus() {
        List<String> sidebar = Utils.getSidebarLines();
        if (sidebar == null || sidebar.isEmpty()) {
            return -1;
        }
        if (!Utils.stripColor(sidebar.get(0)).startsWith("BUILD BATTLE")) {
            return -1;
        }
        for (int i = 0; i < sidebar.size() - 1; i++) {
            String currentLine = Utils.stripColor(sidebar.get(i));
            String nextLine = Utils.stripColor(sidebar.get(i + 1));

            if (currentLine.startsWith("Round:")) {
                if (nextLine.startsWith("Starts In: 00:03") && Utils.stripColor(sidebar.get(i + 3)).startsWith("Theme:")) {
                    return 4;
                }
                if (nextLine.startsWith("Starts In:")) {
                    return 1;
                }
                if (nextLine.startsWith("Time Left:")) {
                    return 2;
                }
                if (nextLine.startsWith("Judging:")) {
                    return 3;
                }
            }
        }
        return 0;
    }

    public BlockPos findCenter(Vec3d position) {
        BlockPos closestPos = null;
        double closestDistSq = Double.MAX_VALUE;
        double maxDistance = 30.0;
        double maxDistSq = maxDistance * maxDistance;

        for (BlockPos pos : PLATFORM_POSITIONS) {
            double dx = pos.getX() - position.xCoord;
            double dy = pos.getY() - position.yCoord;
            double dz = pos.getZ() - position.zCoord;
            double distSq = Math.abs(dx * dx + dy * dy + dz * dz);

            if (distSq <= maxDistSq && distSq < closestDistSq) {
                closestDistSq = distSq;
                closestPos = pos;
            }
        }
        return closestPos;
    }

    public ConcurrentHashMap<BlockPos, BuildBlockInfo> getBuildInfo(BlockPos centerPos) {
        ConcurrentHashMap<BlockPos, BuildBlockInfo> blockInfo = new ConcurrentHashMap<>();
        int startX = centerPos.getX() - 3;
        int endX = centerPos.getX() + 3;
        int startZ = centerPos.getZ() - 3;
        int endZ = centerPos.getZ() + 3;

        int startY = centerPos.getY() + 1;
        int endY = startY + 30;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(currentPos);
                    if (state.getBlock() == Blocks.AIR) {
                        continue;
                    }
                    blockInfo.put(currentPos, new BuildBlockInfo(state));
                }
            }
        }

        return blockInfo;
    }

    public boolean autoEnabled() {
        return autoSwap.isToggled() && hoverPlace.isToggled();
    }

    public boolean holdingSameBlock(BlockState requiredState) {
        if (mc.player == null || requiredState == null) {
            return false;
        }

        ItemStack heldItem = mc.player.getHeldItem();
        if (heldItem == null) {
            return false;
        }

        Item item = heldItem.getItem();
        Block requiredBlock = requiredState.getBlock();

        if ((requiredBlock == Blocks.water || requiredBlock == Blocks.flowing_water) && item == Items.water_bucket) {
            return true;
        }

        if (!(item instanceof BlockItem)) {
            return false;
        }

        Block heldBlock = ((BlockItem) item).getBlock();
        int heldMeta = heldItem.getItemDamage();
        int requiredMeta = requiredBlock.getMetaFromState(requiredState);

        if (requiredBlock == Blocks.leaves || requiredBlock == Blocks.leaves2) {
            requiredMeta = requiredMeta & 3;
            heldMeta = heldMeta & 3;
        }

        if (removeMeta(heldBlock)) {
            heldMeta = 0;
            requiredMeta = 0;
        }

        return heldBlock == requiredBlock && heldMeta == requiredMeta;
    }

    public HitResult getLookInfo() {
        HitResult movingObjectPosition = mc.crosshairTargetr;
        if (movingObjectPosition == null || movingObjectPosition.typeOfHit != HitResult.Type.BLOCK || movingObjectPosition.getBlockPos() == null) {
            return null;
        }
        return mc.crosshairTargetr;
    }

    private int getSlot(Block block, int meta) {
        if (removeMeta(block)) {
            meta = 0;
        }
        if (block == Blocks.leaves || block == Blocks.leaves2) {
            meta &= 3;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof BlockItem && itemStack.stackSize > 0) {
                Block invBlock = ((BlockItem) itemStack.getItem()).getBlock();
                int invMeta = itemStack.getItemDamage();

                if (removeMeta(block)) {
                    invMeta = 0;
                }

                if (invBlock == Blocks.leaves || invBlock == Blocks.leaves2) {
                    invMeta &= 3;
                }

                if (invBlock == block && invMeta == meta) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean removeMeta(Block block) {
        return (block instanceof StairsBlock || block instanceof DoublePlantBlock || block instanceof FlowerBlock || block instanceof BlockSkull || block instanceof BlockLadder || block instanceof BlockPumpkin || block instanceof BlockCauldron || block instanceof BlockRail || block instanceof BlockRailBase || block instanceof BlockTripWireHook || block instanceof BlockTripWire || block instanceof BlockDispenser || block instanceof BlockDropper || block instanceof BlockHopper || block instanceof BlockTorch || block instanceof BlockButton || block instanceof BlockLever || block instanceof BlockTrapDoor || block instanceof BlockSlab);
    }

    private boolean correctPlaceState(BlockState requiredState, BlockPos blockPos, Direction enumFacing, Vec3d hitVec, ItemStack heldItem) {
        if (requiredState == null || blockPos == null || enumFacing == null || hitVec == null || heldItem == null || !(heldItem.getItem() instanceof BlockItem)) {
            return false;
        }

        if (requiredState.getBlock() instanceof BlockLeaves || requiredState.getBlock() instanceof BlockButton) {
            return true;
        }

        BlockItem itemBlock = (BlockItem) heldItem.getItem();
        Block block = itemBlock.getBlock();
        int meta = heldItem.getItemDamage();

        Vec3d relativeHitVec = hitVec.subtract(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        BlockState simulatedState = block.onBlockPlaced(mc.world, blockPos, enumFacing,
                (float) relativeHitVec.xCoord, (float) relativeHitVec.yCoord, (float) relativeHitVec.zCoord, meta, mc.player);

        if (simulatedState == null) {
            return false;
        }

        if (simulatedState.getBlock() != requiredState.getBlock()) {
            return false;
        }

        int simulatedMeta = simulatedState.getBlock().getMetaFromState(simulatedState);
        int requiredMeta = requiredState.getBlock().getMetaFromState(requiredState);
        if (simulatedMeta != requiredMeta) {
            return false;
        }
        if (simulatedState.getProperties().containsKey(BlockDirectional.FACING) && requiredState.getProperties().containsKey(BlockDirectional.FACING)) {
            Direction simulatedFacing = simulatedState.getValue(BlockDirectional.FACING);
            Direction requiredFacing = requiredState.getValue(BlockDirectional.FACING);
            if (simulatedFacing != requiredFacing) {
                return false;
            }
        }
        if (simulatedState.getBlock() instanceof StairsBlock && requiredState.getBlock() instanceof StairsBlock) {
            Direction simulatedFacing = simulatedState.getValue(StairsBlock.FACING);
            Direction requiredFacing = requiredState.getValue(StairsBlock.FACING);
            StairsBlock.EnumHalf simulatedHalf = simulatedState.getValue(StairsBlock.HALF);
            StairsBlock.EnumHalf requiredHalf = requiredState.getValue(StairsBlock.HALF);

            if (simulatedFacing != requiredFacing || simulatedHalf != requiredHalf) {
                return false;
            }
        }

        return true;
    }

    class BuildBlockInfo {
        public BlockState requiredState;
        public boolean isPlaced;

        public BuildBlockInfo(BlockState state) {
            this.requiredState = state;
            this.isPlaced = false;
        }
    }
}