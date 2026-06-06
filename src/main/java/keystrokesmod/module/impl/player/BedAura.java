mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
package keystrokesmod.module.impl.player;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.event.ClientRotationEvent;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.event.PreAttackEvent;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.event.PrePlayerInteractEvent;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.event.PreSlotScrollEvent;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.event.SlotUpdateEvent;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
// import keystrokesmod.mixin.impl.accessor.IAccessorEntityRenderer;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.Module;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.ModuleManager;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.impl.combat.KillAura;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.impl.render.BlockOverlay;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.setting.impl.ButtonSetting;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.setting.impl.ColorSetting;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.setting.impl.DescriptionSetting;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.setting.impl.GroupSetting;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.module.setting.impl.SliderSetting;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.utility.BlockUtils;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.utility.RotationUtils;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import keystrokesmod.utility.Utils;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.block.Block;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.block.BedBlock;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerInventory;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import net.minecraft.util.*;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
import java.util.*;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
public class BedAura extends Module {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final SliderSetting fov;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final SliderSetting range;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final SliderSetting rate;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final SliderSetting breakDelay;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final SliderSetting breakSpeed;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ButtonSetting whitelistOwnBed;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ButtonSetting prioritizeKillAura;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final GroupSetting swapGroup;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ButtonSetting switchBackWhenDone;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ButtonSetting overrideSwapBack;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ButtonSetting renderOutline;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final ColorSetting outlineColor;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private static final int MS_PER_TICK = 50;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private static final double BED_FIND_EXTRA_BLOCKS = 1.0;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private static final double OWN_BED_PROTECTION_RADIUS_SQ = 800.0;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private final List<BlockPos[]> bedPairsCache = new ArrayList<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private int scanCooldown;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private BlockPos targetPos;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Vec3d targetHitVec;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Direction targetSide;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean miningActive;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private int hotbarProgrammaticDepth;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean hasSwapped;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private int previousSlot = -1;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private BlockPos spawnAnchor;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean pendingSpawnAnchorCapture;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean waitingForRespawn;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private long respawnMessageTime;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public BedAura() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        super("Bed Aura", category.player);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(breakSpeed = new SliderSetting("Break speed", "x", 1.0, 1.0, 2.0, 0.02));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(breakDelay = new SliderSetting("Break delay", "ms", 250.0, 0.0, 250.0, 50.0));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(range = new SliderSetting("Range", " blocks", 4.5, 2.0, 6.0, 0.1));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(fov = new SliderSetting("FOV", "", 180.0, 30.0, 360.0, 1.0));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(rate = new SliderSetting("Scan rate", "ms", 250.0, 50.0, 2000.0, 50.0));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(whitelistOwnBed = new ButtonSetting("Whitelist own bed", true));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(prioritizeKillAura = new ButtonSetting("Prioritize KillAura", false));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(swapGroup = new GroupSetting("Swap"));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(switchBackWhenDone = new ButtonSetting(swapGroup, "Switch back when done", true, "Swap to previous slot"));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(overrideSwapBack = new ButtonSetting(swapGroup, "Override swap back", true));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(renderOutline = new ButtonSetting("Render block outline", true));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        this.registerSetting(outlineColor = new ColorSetting("Outline color", 255, 64, 64, 229));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    @Override
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void guiUpdate() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        outlineColor.setVisible(renderOutline.isToggled(), this);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    @Override
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void onDisable() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        resetSpawnTracking();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        bedPairsCache.clear();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        scanCooldown = 0;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    @Override
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void onUpdate() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!Utils.nullCheck()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (pendingSpawnAnchorCapture && Utils.getBedwarsStatus() == 2) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            spawnAnchor = mc.player.getPosition();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            pendingSpawnAnchorCapture = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void onWorldJoin(Object e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (e.entity == mc.player) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetSpawnTracking();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void onChat(Object event) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!Utils.nullCheck()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        String strippedMessage = Utils.stripColor(event.message.getUnformattedText());
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (strippedMessage.startsWith(" ") && strippedMessage.contains("Protect your bed and destroy the enemy beds.")) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            pendingSpawnAnchorCapture = true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            waitingForRespawn = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        else if (strippedMessage.equals("You will respawn because you still have a bed!")) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            waitingForRespawn = true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            respawnMessageTime = System.currentTimeMillis();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        else if (strippedMessage.equals("You have respawned!") && waitingForRespawn && Utils.timeBetween(System.currentTimeMillis(), respawnMessageTime) <= 12000) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            pendingSpawnAnchorCapture = true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            waitingForRespawn = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }public void onPrePlayerInteract(PrePlayerInteractEvent e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        applyMiningKeyState();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }// TODO: Replace MouseEvent
    public void onMouse(Object e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldSuppressManualMouse()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (e.button == 0 || e.button == 1) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            e.setCanceled(true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }public void onPreAttack(PreAttackEvent e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldSuppressManualMouse()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        e.setCanceled(true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }public void onSlotScroll(PreSlotScrollEvent e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldSuppressManualMouse()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (hasSwapped && overrideSwapBack.isToggled() && Utils.nullCheck()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            int slot = Integer.compare(e.slot, 0);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            previousSlot = Math.floorMod(mc.player.inventory.currentItem - slot, PlayerInventory.getHotbarSize());
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        e.setCanceled(true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }public void onSlotUpdate(SlotUpdateEvent e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldSuppressManualMouse() || hotbarProgrammaticDepth > 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (hasSwapped && overrideSwapBack.isToggled()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            previousSlot = e.slot;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        e.setCanceled(true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean shouldSuppressManualMouse() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return miningActive && isEnabled() && Utils.nullCheck() && mc.currentScreen == null && canMineBlocks() && !shouldYieldToKillAura();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void applyMiningKeyState() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!canMineBlocks() || shouldYieldToKillAura()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            if (miningActive) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!miningActive || !isEnabled() || !Utils.nullCheck() || mc.currentScreen != null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        int atk = mc.gameSettings.keyBindAttack.getKeyCode();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        int use = mc.gameSettings.keyBindUseItem.getKeyCode();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        InputUtil.setKeyPressed(atk, false);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        InputUtil.setKeyPressed(use, false);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        InputUtil.setKeyPressed(atk, true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public BlockPos getAuraTargetPos() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return miningActive && canMineBlocks() ? targetPos : null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public boolean isActivelyMining() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return miningActive && isEnabled() && Utils.nullCheck() && mc.currentScreen == null && canMineBlocks() && !shouldYieldToKillAura();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public boolean shouldOverrideFastMine() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return isActivelyMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public float getBreakSpeedMultiplier() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float multiplier = (float) breakSpeed.getInput();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return multiplier > 1.0f ? multiplier : 1.0f;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public int getBreakDelayTicks() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return Math.max(0, Math.min(5, (int) (breakDelay.getInput() / 50.0)));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public float getAuraBreakProgress() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!canMineBlocks() || !miningActive || mc.playerController == null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return 0f;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        IAccessorPlayerControllerMP pc = (IAccessorPlayerControllerMP) mc.playerController;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos currentBlock = pc.getCurrentBlock();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (targetPos == null || currentBlock == null || !targetPos.equals(currentBlock)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return 0f;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return pc.getCurBlockDamageMP();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public boolean shouldOverrideMouseOver() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return isEnabled() && miningActive && canMineBlocks() && targetPos != null && targetHitVec != null && targetSide != null && Utils.nullCheck() && !shouldYieldToKillAura();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void modifyMouseOverFromGetMouseOver(float partialTicks) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldOverrideMouseOver()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (mc.getRenderViewEntity() == null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        HitResult mop = new MovingObjectPosition(targetHitVec, targetSide, targetPos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        mc.objectMouseOver = mop;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        mc.pointedEntity = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        EntityRenderer renderer = mc.entityRenderer;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (renderer instanceof IAccessorEntityRenderer) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            ((IAccessorEntityRenderer) renderer).setPointedEntity(null);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }public void onClientRotation(ClientRotationEvent e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!isEnabled() || !Utils.nullCheck() || mc.currentScreen != null || !canMineBlocks()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (shouldYieldToKillAura()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (e.scriptRotations) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double reach = range.getInput();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double reachSq = reach * reach;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (--scanCooldown <= 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            scanCooldown = Math.max(1, (int) Math.round(rate.getInput() / (double) MS_PER_TICK));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            rebuildBedPairsCache(reach + BED_FIND_EXTRA_BLOCKS);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (bedPairsCache.isEmpty()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Choice best = chooseBestTarget(reachSq);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (best == null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            resetMining();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetPos = best.pos;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetHitVec = best.hitVec;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetSide = best.side;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        miningActive = true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        equipBestHotbarTool(BlockUtils.getBlock(targetPos));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float baseYaw = e.yaw != null ? e.yaw : RotationUtils.serverRotations[0];
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float basePitch = e.pitch != null ? e.pitch : RotationUtils.serverRotations[1];
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float[] r = RotationUtils.getRotationsToPoint(
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                targetHitVec.xCoord, targetHitVec.yCoord, targetHitVec.zCoord,
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                baseYaw, basePitch
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        );
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        e.setYaw(r[0]);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        e.setPitch(r[1]);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    public void onRenderWorldLast(Object e) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!isEnabled() || !renderOutline.isToggled() || !miningActive || targetPos == null || !Utils.nullCheck() || !canMineBlocks()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockState st = mc.world.getBlockState(targetPos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Block b = st.getBlock();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (b == null || b == Blocks.AIR) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        int c = outlineColor.getColor();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockOverlay.renderBlockOutline(targetPos, c, c, 2.0f, true);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void resetMining() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        miningActive = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (switchBackWhenDone.isToggled() && previousSlot != -1 && Utils.nullCheck()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            setSlot(previousSlot);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        InputUtil.setKeyPressed(mc.gameSettings.keyBindAttack.getKeyCode(), GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        InputUtil.setKeyPressed(mc.gameSettings.keyBindUseItem.getKeyCode(), GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        hotbarProgrammaticDepth = 0;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetPos = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetHitVec = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        targetSide = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        hasSwapped = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        previousSlot = -1;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void rebuildBedPairsCache(double searchRange) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        bedPairsCache.clear();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Set<BlockPos> seenFeet = new HashSet<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        int ri = (int) Math.ceil(searchRange);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos origin = new BlockPos(mc.player);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        for (int dx = -ri; dx <= ri; dx++) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            for (int dy = -ri; dy <= ri; dy++) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                for (int dz = -ri; dz <= ri; dz++) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    BlockPos p = origin.add(dx, dy, dz);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    BlockPos[] pair = footHeadPair(p);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (pair == null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    BlockPos foot = pair[0];
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (seenFeet.contains(foot)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (!bedInSearchRange(pair, searchRange)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    Vec3d center = bedCenter(pair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (!inFov(center, (float) fov.getInput())) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    seenFeet.add(foot);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    bedPairsCache.add(pair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        removeOwnBedPair();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private BlockPos[] footHeadPair(BlockPos at) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockState st = mc.world.getBlockState(at);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!(st.getBlock() instanceof BedBlock)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BedBlock.EnumPartType part = (BedBlock.EnumPartType) st.getValue(BedBlock.PART);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Direction facing = (Direction) st.getValue(BedBlock.FACING);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos foot = part == BedBlock.EnumPartType.FOOT ? at : at.offset(facing.getOpposite());
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockState footSt = mc.world.getBlockState(foot);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!(footSt.getBlock() instanceof BedBlock)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (footSt.getValue(BedBlock.PART) != BedBlock.EnumPartType.FOOT) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Direction footFacing = (Direction) footSt.getValue(BedBlock.FACING);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos head = foot.offset(footFacing);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockState hs = mc.world.getBlockState(head);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!(hs.getBlock() instanceof BedBlock)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (hs.getValue(BedBlock.PART) != BedBlock.EnumPartType.HEAD) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (hs.getValue(BedBlock.FACING) != footFacing) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return new BlockPos[]{foot, head};
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Vec3d bedCenter(BlockPos[] pair) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Box a = BlockUtils.unionBlockBounds(pair[0], pair[1]);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return new Vec3d((a.minX + a.maxX) * 0.5, (a.minY + a.maxY) * 0.5, (a.minZ + a.maxZ) * 0.5);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean bedInSearchRange(BlockPos[] pair, double searchRadius) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double r2 = searchRadius * searchRadius + 1e-4;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Box u = BlockUtils.unionBlockBounds(pair[0], pair[1]);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d onBox = RotationUtils.closestPointOnAabb(u, eye);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (eye.squareDistanceTo(onBox) <= r2) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d mid = new Vec3d((u.minX + u.maxX) * 0.5, (u.minY + u.maxY) * 0.5, (u.minZ + u.maxZ) * 0.5);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return eye.squareDistanceTo(mid) <= r2;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean inFov(Vec3d worldPoint, float fovDeg) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (fovDeg >= 360) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d eyes = mc.player.getPositionEyes(1f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d look = mc.player.getLook(1f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d to = worldPoint.subtract(eyes);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double len = to.lengthVector();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (len < 1e-6) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        to = new Vec3d(to.xCoord / len, to.yCoord / len, to.zCoord / len);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double dot = look.xCoord * to.xCoord + look.yCoord * to.yCoord + look.zCoord * to.zCoord;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double ang = Math.acos(MathHelper.clamp_double(dot, -1.0, 1.0)) * (180.0 / Math.PI);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return ang <= fovDeg * 0.5;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Choice chooseBestTarget(double reachSq) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        IAccessorPlayerControllerMP pc = (IAccessorPlayerControllerMP) mc.playerController;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float curProg = pc.getCurBlockDamageMP();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos breaking = pc.getCurrentBlock();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        List<BlockPos[]> exposed = new ArrayList<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        List<BlockPos[]> covered = new ArrayList<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        for (BlockPos[] pair : bedPairsCache) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            if (isBedExposed(pair)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                exposed.add(pair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            } else {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                covered.add(pair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        sortBedsByEyeDistance(exposed);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        sortBedsByEyeDistance(covered);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Choice c = pickBestOnClosestBedWithCandidates(exposed, reachSq, curProg, breaking);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (c != null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return c;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return pickBestOnClosestBedWithCandidates(covered, reachSq, curProg, breaking);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void sortBedsByEyeDistance(List<BlockPos[]> pairs) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d eye = mc.player.getPositionEyes(1f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        pairs.sort(Comparator.comparingDouble(p -> eye.squareDistanceTo(bedCenter(p))));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Choice pickBestOnClosestBedWithCandidates(List<BlockPos[]> sortedPairs, double reachSq, float curProg, BlockPos breaking) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        for (BlockPos[] pair : sortedPairs) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            List<Choice> candidates = buildCandidates(pair, reachSq);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            if (candidates.isEmpty()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            Choice best = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            double bestScore = Double.POSITIVE_INFINITY;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            for (Choice ch : candidates) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                double score = scoreChoice(ch, curProg, breaking);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                if (score < bestScore) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    bestScore = score;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    best = ch;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return best;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private double scoreChoice(Choice ch, float curProg, BlockPos breaking) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Block block = BlockUtils.getBlock(ch.pos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float bestHotbar = BlockUtils.maxDigRateAcrossSlots(block, PlayerInventory.getHotbarSize());
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (bestHotbar <= 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return Double.POSITIVE_INFINITY;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double timeEst = 1.0 / bestHotbar;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (breaking != null && breaking.equals(ch.pos) && curProg > 0.02f) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            timeEst -= curProg * 12.0;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d eye = mc.player.getPositionEyes(1f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        timeEst += eye.squareDistanceTo(ch.hitVec) * 0.002;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return timeEst;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private List<Choice> buildCandidates(BlockPos[] pair, double reachSq) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        List<Choice> out = new ArrayList<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        boolean exposed = isBedExposed(pair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (exposed) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            for (BlockPos bp : pair) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                addBlockCandidate(bp, reachSq, out);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        } else {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            Set<BlockPos> seen = new HashSet<>();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            for (BlockPos bp : pair) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                for (Direction f : Direction.values()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (f == Direction.DOWN) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    BlockPos n = bp.offset(f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (seen.contains(n)) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    BlockState st = mc.world.getBlockState(n);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    Block b = st.getBlock();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (b == Blocks.AIR || b instanceof BedBlock) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    float hard = b.getBlockHardness(mc.world, n);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    if (hard < 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                        continue;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    seen.add(n);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    addBlockCandidate(n, reachSq, out);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return out;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean isBedExposed(BlockPos[] pair) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        for (BlockPos bp : pair) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            for (Direction f : Direction.values()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                BlockPos n = bp.offset(f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                if (mc.world.getBlockState(n).getBlock() == Blocks.AIR) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                    return true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void addBlockCandidate(BlockPos pos, double reachSq, List<Choice> out) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockState st = mc.world.getBlockState(pos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Block block = st.getBlock();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (block == Blocks.AIR) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        float hard = block.getBlockHardness(mc.world, pos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (hard < 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Box bb = BlockUtils.getBlockSelectionBox(pos);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (bb == null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d eye = mc.player.getPositionEyes(1.0f);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d hit = RotationUtils.closestPointOnAabb(bb, eye);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (eye.squareDistanceTo(hit) > reachSq + 1e-3) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        HitResult trace = block.collisionRayTrace(mc.world, pos, eye, hit.addVector(
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                (hit.xCoord - eye.xCoord) * 0.01,
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                (hit.yCoord - eye.yCoord) * 0.01,
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                (hit.zCoord - eye.zCoord) * 0.01
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        ));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Direction side = BlockUtils.facingFromBlockCenterToPoint(pos, hit);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (trace != null && trace.hitVec != null && trace.sideHit != null && pos.equals(trace.getBlockPos())) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            hit = trace.hitVec;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            side = trace.sideHit;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (block instanceof BedBlock && side == Direction.DOWN) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        out.add(new Choice(pos, hit, side));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void equipBestHotbarTool(Block block) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        int slot = Utils.getTool(block);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (slot < 0) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (previousSlot == -1 && slot != mc.player.inventory.currentItem) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            previousSlot = mc.player.inventory.currentItem;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (slot != mc.player.inventory.currentItem) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            setSlot(slot);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void setSlot(int slot) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (slot == -1 || slot == mc.player.inventory.currentItem) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        hotbarProgrammaticDepth++;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        try {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            mc.player.inventory.currentItem = slot;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            hasSwapped = true;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            ((IAccessorPlayerControllerMP) mc.playerController).callSyncCurrentPlayItem();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        } finally {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            hotbarProgrammaticDepth--;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean canMineBlocks() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return mc.player.capabilities.allowEdit
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && !mc.player.capabilities.isCreativeMode
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && !mc.player.isSpectator();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean shouldYieldToKillAura() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!prioritizeKillAura.isToggled()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return ModuleManager.killAura != null
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && ModuleManager.killAura.isEnabled()
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && KillAura.target != null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void resetSpawnTracking() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        spawnAnchor = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        pendingSpawnAnchorCapture = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        waitingForRespawn = false;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        respawnMessageTime = 0L;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private void removeOwnBedPair() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (!shouldWhitelistOwnBed() || bedPairsCache.isEmpty()) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            return;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        BlockPos[] ownBedPair = null;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        double closestDistance = Double.POSITIVE_INFINITY;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Vec3d spawnCenter = spawnAnchorCenter();
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        for (BlockPos[] pair : bedPairsCache) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            double distance = spawnCenter.squareDistanceTo(bedCenter(pair));
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            if (distance < closestDistance) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                closestDistance = distance;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                ownBedPair = pair;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        if (ownBedPair != null) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            bedPairsCache.remove(ownBedPair);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private boolean shouldWhitelistOwnBed() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return whitelistOwnBed.isToggled()
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && spawnAnchor != null
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && Utils.getBedwarsStatus() == 2
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
                && mc.player.getDistanceSq(spawnAnchor) <= OWN_BED_PROTECTION_RADIUS_SQ;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private Vec3d spawnAnchorCenter() {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        return new Vec3d(spawnAnchor.getX() + 0.5, spawnAnchor.getY() + 0.5, spawnAnchor.getZ() + 0.5);
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    private static final class Choice {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        final BlockPos pos;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        final Vec3d hitVec;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        final Direction side;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;

mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        Choice(BlockPos pos, Vec3d hitVec, Direction side) {
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            this.pos = pos;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            this.hitVec = hitVec;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
            this.side = side;
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
        }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
    }
mport net.minecraft.util.math.Vec3d;
mport net.minecraft.util.math.Direction;
}
