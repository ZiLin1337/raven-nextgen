package keystrokesmod.module.impl.player;

import keystrokesmod.event.PrePlayerInteractEvent;
import keystrokesmod.event.SendPacketEvent;
// Removed accessor
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.BlockListSetting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

public class AutoSwap extends Module {
    private final ButtonSetting useBlockWhitelist;
    private final BlockListSetting blockWhitelist;

    private ItemStack trackedStack;
    private int lastPlaceSlot = -1;
    private int lastSwapSlot = -1;
    private long lastSwapTime;

    public AutoSwap() {
        super("Auto Swap", category.player);

        this.registerSetting(useBlockWhitelist = new ButtonSetting("Use block whitelist", false));
        this.registerSetting(blockWhitelist = new BlockListSetting("Whitelisted blocks", "Blocks", "Blocks.Block whitelist", "Blocks.Whitelisted blocks"));
        blockWhitelist.visible = false;
        this.closetModule = true;
    }

    @Override
    public void guiUpdate() {
        blockWhitelist.setVisible(useBlockWhitelist.isToggled(), this);
    }

    @Override
    public void onEnable() {
        resetState();
    }

    @Override
    public void onDisable() {
        resetState();
    }

    
    public void onSendPacket(SendPacketEvent e) {
        if (!Utils.nullCheck() || !(e.getPacket() instanceof PlayerInteractBlockC2SPacket) {
            return;
        }

        PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket) e.getPacket();
        if (packet.getPlacedBlockDirection() == 255) {
            return;
        }

        ItemStack stack = packet.getStack();
        if (stack == null || !(stack.getItem() instanceof BlockItem) {
            return;
        }

        trackedStack = stack.copy();
        trackedStack.stackSize = 1;
        lastPlaceSlot = mc.player.inventory.currentItem;
    }

    
    public void onPrePlayerInteract(PrePlayerInteractEvent e) {
        if (!Utils.nullCheck() {
            resetState();
            return;
        }

        if (!mc.inGameHasFocus || mc.currentScreen != null || !Utils.isBindDown(mc.options.keyBindUseItem) {
            return;
        }

        if (trackedStack == null || lastPlaceSlot == -1 || mc.player.inventory.currentItem != lastPlaceSlot) {
            return;
        }

        ItemStack held = mc.player.getHeldItem();
        if (held != null && held.stackSize > 0) {
            return;
        }

        if (!isWhitelistedBlock(trackedStack) {
            return;
        }

        long now = System.currentTimeMillis();
        for (int slot = 8; slot >= 0; --slot) {
            if (slot == lastSwapSlot && now - lastSwapTime < 300L) {
                continue;
            }

            ItemStack candidate = mc.player.inventory.getStackInSlot(slot);
            if (!matchesTrackedStack(candidate) {
                continue;
            }

            swapToSlot(slot);
            lastSwapSlot = slot;
            lastSwapTime = now;
            break;
        }
    }

    private boolean isWhitelistedBlock(ItemStack stack) {
        if (!useBlockWhitelist.isToggled() {
            return true;
        }

        if (stack == null || !(stack.getItem() instanceof BlockItem) {
            return false;
        }

        Block block = ((BlockItem) stack.getItem()).getBlock();
        Object registryName = Registries.BLOCK.getNameForObject(block);
        if (block == null || registryName == null) {
            return false;
        }

        String registryId = registryName.toString();
        int meta = stack.getMetadata();
        String storageId = meta != 0 ? registryId + ":" + meta : registryId;
        return blockWhitelist.contains(storageId) || blockWhitelist.contains(registryId);
    }

    private boolean matchesTrackedStack(ItemStack stack) {
        if (trackedStack == null || stack == null || stack.getItem() != trackedStack.getItem() {
            return false;
        }

        if (stack.getHasSubtypes() && stack.getMetadata() != trackedStack.getMetadata() {
            return false;
        }

        return ItemStack.areItemStackTagsEqual(stack, trackedStack);
    }

    private void swapToSlot(int slot) {
        if (slot == -1 || slot == mc.player.inventory.currentItem) {
            return;
        }

        mc.player.inventory.currentItem = slot;
        ((IAccessorPlayerControllerMP) mc.interactionManager).callSyncCurrentPlayItem();
    }

    private void resetState() {
        trackedStack = null;
        lastPlaceSlot = -1;
        lastSwapSlot = -1;
        lastSwapTime = 0L;
    }
}
