package keystrokesmod.module.impl.combat;
import keystrokesmod.event.SendPacketEvent;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerInputEvent;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.CombatTargeting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

/**
 * High combat protocol sword blocking (1.21.4).
 * In modern MinecraftClient, swords cannot block - only shields can.
 * This module simulates sword blocking by using a shield in the offhand.
 * Inspired by LiquidBounce's SwordBlock + KillAuraAutoBlock.
 */
public class Autoblock extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final SliderSetting mode;
    private final SliderSetting unblockMode;
    private final SliderSetting range;
    private final ButtonSetting autoSwitchShield;
    private final ButtonSetting onlyWhenDamage;
    private final ButtonSetting keepBlock;
    private final ButtonSetting swordOnly;
    private final ButtonSetting requireLmb;
    private final ButtonSetting requireRmb;

    private final String[] modes = {"Interact", "Packet", "Hypixel"};
    private final String[] unblockModes = {"StopUsingItem", "SwitchSlot", "None"};
    private final String[] blockModes = {"Interact", "Packet", "Hypixel"};

    private boolean blocking = false;
    private boolean wasBlocking = false;
    private int blockingTicks = 0;
    private int unblockTimer = 0;
    private LivingEntity currentTarget;

    public Autoblock() {
        super("Auto Block", category.combat);
        this.registerSetting(mode = new SliderSetting("Mode", 0, modes));
        this.registerSetting(unblockMode = new SliderSetting("Unblock Mode", 0, unblockModes));
        this.registerSetting(range = new SliderSetting("Range", 4.0, 2.0, 6.0, 0.1));
        this.registerSetting(autoSwitchShield = new ButtonSetting("Auto switch shield", true));
        this.registerSetting(onlyWhenDamage = new ButtonSetting("Only when damaged", false));
        this.registerSetting(keepBlock = new ButtonSetting("Keep block", true));
        this.registerSetting(swordOnly = new ButtonSetting("Sword only", true));
        this.registerSetting(requireLmb = new ButtonSetting("Require left click", true));
        this.registerSetting(requireRmb = new ButtonSetting("Require right click", false));
    }

    @Override
    public void onDisable() {
        stopBlocking(true);
        blocking = false;
        wasBlocking = false;
    }

    @EventHandler
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) return;

        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // Check conditions
        if (swordOnly.isToggled() && !isHoldingSwordOrShield()) {
            stopBlocking(keepBlock.isToggled());
            return;
        }

        currentTarget = CombatTargeting.findTarget(range.getInput() * range.getInput(), true);
        boolean killAuraActive = ModuleManager.killAura != null
                && ModuleManager.killAura.isEnabled()
                && KillAura.target != null;

        boolean lmbDown = mc.mouse.isLeftButtonPressed() || killAuraActive;
        boolean rmbDown = mc.mouse.isRightButtonPressed();

        // Check use conditions
        boolean shouldBlock = currentTarget != null;
        if (requireLmb.isToggled() && !lmbDown) shouldBlock = false;
        if (requireRmb.isToggled() && !rmbDown) shouldBlock = false;

        // Only when damaged check
        if (onlyWhenDamage.isToggled() && player.hurtTime == 0) {
            shouldBlock = false;
        }

        if (shouldBlock) {
            if (!blocking) {
                startBlocking(player);
            }
            blockingTicks++;

            // Hypixel mode: re-interact every 5 ticks
            if (modes[(int) mode.getInput()].equals("Hypixel") && blockingTicks % 5 == 0) {
                interactItem(player, Hand.MAIN_HAND);
            }
        } else {
            if (blocking) {
                if (keepBlock.isToggled() && currentTarget == null) {
                    // Keep blocking when no target but still holding sword
                    if (!blocking) {
                        startBlocking(player);
                    }
                } else {
                    stopBlocking(false);
                }
            }
        }
    }

    @EventHandler
    public void onSendPacket(SendPacketEvent e) {
        if (!blocking) return;

        // When attack packet is sent, we may want to unblock briefly
        if (e.getPacket() instanceof net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket) {
            int um = (int) unblockMode.getInput();
            if (um == 1) { // SwitchSlot
                unblockBySwitchSlot();
            } else if (um == 2) { // None
                // Don't unblock
            } else { // StopUsingItem (default)
                stopBlocking(false);
            }
        }
    }

    @EventHandler
    public void onPrePlayerInput(PrePlayerInputEvent e) {
        if (unblockTimer > 0) {
            unblockTimer--;
            if (unblockTimer == 0 && blocking) {
                startBlocking(mc.player);
            }
        }
    }

    private void startBlocking(ClientPlayerEntity player) {
        if (player == null || player.isRemoved()()) return;

        // Check if we have a shield in offhand or mainhand
        boolean canBlock = canBlockWithCurrentItems(player);

        if (!canBlock) {
            // Try to auto-switch a shield from inventory to offhand
            if (autoSwitchShield.isToggled() && tryEquipShield(player)) {
                canBlock = true;
            }
        }

        if (!canBlock) return;

        String modeName = modes[(int) mode.getInput()];
        Hand blockHand = getBlockHand(player);

        switch (modeName) {
            case "Interact":
                interactItem(player, blockHand);
                break;
            case "Packet":
                sendBlockPacket(player, blockHand);
                break;
            case "Hypixel":
                // Interact with entity first, then block
                if (currentTarget != null) {
                    mc.interactionManager.interactEntity(player, currentTarget, Hand.MAIN_HAND);
                }
                interactItem(player, blockHand);
                break;
        }

        blocking = true;
        blockingTicks = 0;
        wasBlocking = true;

        // Enforce use key for visual
        if (!modeName.equals("Packet")) {
            player.setCurrentHand(blockHand);
        }
    }

    private void stopBlocking(boolean force) {
        if (!blocking && !force) return;

        ClientPlayerEntity player = mc.player;
        if (player != null) {
            String um = unblockModes[(int) unblockMode.getInput()];
            switch (um) {
                case "StopUsingItem":
                    player.stopUsingItem();
                    break;
                case "SwitchSlot":
                    unblockBySwitchSlot();
                    break;
                case "None":
                    // Just release visual
                    break;
            }
        }

        // Force stop using item
        if (player != null) {
            player.clearActiveItem();
        }

        blocking = false;
        blockingTicks = 0;
        unblockTimer = 2; // Re-block after 2 ticks
    }

    private void unblockBySwitchSlot() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        int currentSlot = player.getInventory().selectedSlot;
        int nextSlot = (currentSlot + 1) % 8;
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(nextSlot));
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
        }
    }

    private void interactItem(ClientPlayerEntity player, Hand hand) {
        if (player == null || mc.interactionManager == null) return;
        ActionResult result = mc.interactionManager.interactItem(player, hand);
        if (result.isAccepted()) {
            player.swingHand(hand);
        }
    }

    /**
     * Sends a PlayerInteractItemC2SPacket directly with sequence for better synchronization.
     */
    private void sendBlockPacket(ClientPlayerEntity player, Hand hand) {
        if (player == null || mc.getNetworkHandler() == null) return;
        ClientWorld world = mc.world;
        if (world == null) return;
        mc.getNetworkHandler().sendPacket(
                new PlayerInteractItemC2SPacket(hand, 0, player.getYaw(), player.getPitch())
        );
    }

    /**
     * Checks if any held item can block (has UseAction.BLOCK)
     */
    private boolean canBlockWithCurrentItems(ClientPlayerEntity player) {
        if (player == null) return false;
        // Check main hand
        ItemStack mainHand = player.getMainHandStack();
        if (mainHand.getItem().getUseAction(mainHand) == UseAction.BLOCK) return true;
        // Check offhand
        ItemStack offHand = player.getOffHandStack();
        return offHand.getItem().getUseAction(offHand) == UseAction.BLOCK;
    }

    /**
     * Determines the appropriate hand to block with.
     */
    private Hand getBlockHand(ClientPlayerEntity player) {
        if (player == null) return Hand.MAIN_HAND;
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        // If mainhand is a sword, try offhand shield
        if (mainHand.getItem() instanceof SwordItem && offHand.getItem() instanceof ShieldItem) {
            return Hand.OFF_HAND;
        }
        // If mainhand can block, use it
        if (mainHand.getItem().getUseAction(mainHand) == UseAction.BLOCK) {
            return Hand.MAIN_HAND;
        }
        // Fallback to offhand
        if (offHand.getItem().getUseAction(offHand) == UseAction.BLOCK) {
            return Hand.OFF_HAND;
        }
        return Hand.MAIN_HAND;
    }

    /**
     * Tries to equip a shield from the inventory to the offhand.
     * Returns true if successful.
     */
    private boolean tryEquipShield(ClientPlayerEntity player) {
        if (player == null) return false;

        // Check if shield already in offhand
        if (player.getOffHandStack().getItem() instanceof ShieldItem) return true;

        // Find shield in hotbar (slots 0-8)
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof ShieldItem) {
                // Swap mainhand with offhand (shield goes to offhand)
                if (mc.interactionManager != null) {
                    mc.interactionManager.clickCreativeStack(stack, 40); // 40 = offhand slot
                }
                return true;
            }
        }

        // Find shield anywhere in inventory
        for (int i = 9; i < 36; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof ShieldItem) {
                // Quick move to hotbar first
                if (mc.interactionManager != null) {
                    mc.interactionManager.clickSlot(
                            player.playerScreenHandler.syncId,
                            i,
                            0,
                            net.minecraft.screen.slot.SlotActionType.QUICK_MOVE,
                            player
                    );
                }
                return true;
            }
        }
        return false;
    }

    private boolean isHoldingSwordOrShield() {
        if (mc.player == null) return false;
        return mc.player.getMainHandStack().getItem() instanceof SwordItem
                || mc.player.getOffHandStack().getItem() instanceof ShieldItem
                || mc.player.getMainHandStack().getItem().getUseAction(mc.player.getMainHandStack()) == UseAction.BLOCK;
    }

    /**
     * Expose blocking state for other modules (e.g. KillAura)
     */
    public boolean isBlocking() {
        return blocking;
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }
}
