package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PrePlayerInteractEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.ReflectionUtils;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;


import net.minecraft.inventory.Slot;

import java.lang.reflect.Field;
import java.util.Random;

public class AutoClicker extends Module {
    public SliderSetting targetCPS;
    public ButtonSetting simulateExhaust;
    public ButtonSetting notUsingItem;
    public ButtonSetting breakBlocks;
    public ButtonSetting weaponOnly;
    public ButtonSetting disableCreative;
    public ButtonSetting inventory;
    public SliderSetting inventoryStartDelay;

    private long nextClickTime;
    private long inventoryNextClickTime;
    private boolean isHoldingBlockBreak;

    private Random rand;
    private static Field hoveredSlotField;

    public AutoClicker() {
        super("Auto Clicker", category.combat, 0);
        this.registerSetting(new DescriptionSetting("Best with delay remover."));
        this.registerSetting(targetCPS = new SliderSetting("Target CPS", 10.0, 1.0, 20.0, 0.5));
        this.registerSetting(simulateExhaust = new ButtonSetting("Simulate exhaust", true));
        this.registerSetting(notUsingItem = new ButtonSetting("Not using item", false));
        this.registerSetting(breakBlocks = new ButtonSetting("Break blocks", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(disableCreative = new ButtonSetting("Disable in creative", false));
        this.registerSetting(inventory = new ButtonSetting("Inventory", false));
        this.registerSetting(inventoryStartDelay = new SliderSetting("Start delay", "ms", 100.0, 0.0, 250.0, 10.0));
        this.closetModule = true;
    }

    @Override
    public String getInfo() {
        double cps = targetCPS.getInput();
        return cps == Math.rint(cps) ? Integer.toString((int) cps) : Double.toString(Utils.round(cps, 1));
    }

    @Override
    public void onEnable() {
        this.rand = new Random();
        this.nextClickTime = 0L;
        this.inventoryNextClickTime = 0L;
        this.isHoldingBlockBreak = false;
        ensureHoveredSlotField();
    }

    @Override
    public void onDisable() {
        this.nextClickTime = 0L;
        this.inventoryNextClickTime = 0L;
        this.isHoldingBlockBreak = false;
    }

    
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (!inventory.isToggled()) {
            return;
        }
        if (!Utils.nullCheck()) {
            return;
        }
        if (!(mc.currentScreen instanceof GuiContainer)) {
            inventoryNextClickTime = 0L;
            return;
        }
        if (!GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            inventoryNextClickTime = 0L;
            return;
        }

        ensureHoveredSlotField();
        if (hoveredSlotField == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (inventoryNextClickTime == 0L) {
            inventoryNextClickTime = now + (long) inventoryStartDelay.getInput();
        }

        int clicks = 0;
        while (inventoryNextClickTime <= now) {
            clicks++;
            inventoryNextClickTime += nextDelay();
        }
        if (clicks <= 0) {
            return;
        }

        GuiContainer gui = (GuiContainer) mc.currentScreen;
        Slot slot = getHoveredSlot(gui);
        if (slot == null || slot.slotNumber < 0) {
            return;
        }

        int windowId = gui.inventorySlots.windowId;
        int slotId = slot.slotNumber;
        int mode = net.minecraft.client.gui.GuiScreen.isShiftKeyDown() ? 1 : 0;

        if (mc.playerController == null || mc.player == null) {
            return;
        }

        for (int i = 0; i < clicks; i++) {
            mc.playerController.windowClick(windowId, slotId, 0, mode, mc.player);
        }
    }

    
    public void onPrePlayerInteract(PrePlayerInteractEvent e) {
        if (!Utils.nullCheck()) return;
        if (ModuleManager.killAura != null && ModuleManager.killAura.isEnabled() && KillAura.target != null) return;

        int key = mc.gameSettings.keyBindAttack.getKeyCode();
        if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            long now = System.currentTimeMillis();
            if (nextClickTime == 0) {
                nextClickTime = now + nextDelay();
            }

            int clicks = 0;
            while (nextClickTime <= now) {
                clicks++;
                nextClickTime += nextDelay();
            }

            if (notUsingItem.isToggled() && mc.player.isUsingItem()) return;
            if (disableCreative.isToggled() && mc.player.capabilities.isCreativeMode) return;
            if (mc.currentScreen != null || !mc.inGameHasFocus) return;
            if (weaponOnly.isToggled() && !Utils.holdingWeapon()) return;

            if (breakBlocks.isToggled()) {
                if (!mc.player.capabilities.allowEdit) {
                    if (this.isHoldingBlockBreak) {
                        InputUtil.setKeyPressed(key, false);
                        ReflectionUtils.setButton(0, false);
                        this.isHoldingBlockBreak = false;
                    }
                }
                else if (mc.objectMouseOver != null) {
                BlockPos pos = mc.objectMouseOver.getBlockPos();
                if (pos != null) {
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block != Blocks.AIR && !(block instanceof BlockLiquid)) {
                        if (!this.isHoldingBlockBreak) {
                            InputUtil.setKeyPressed(key, true);
                            ReflectionUtils.setButton(0, true);
                            this.isHoldingBlockBreak = true;
                        }
                        return;
                    }
                    if (this.isHoldingBlockBreak) {
                        InputUtil.setKeyPressed(key, false);
                        ReflectionUtils.setButton(0, false);
                        this.isHoldingBlockBreak = false;
                        return;
                    }
                } else {
                    this.isHoldingBlockBreak = false;
                }
                }
            }

            for (int i = 0; i < clicks; i++) {
                KeyBinding.onTick(key);
                ReflectionUtils.setButton(0, true);
            }
        } else {
            this.nextClickTime = 0L;
            this.isHoldingBlockBreak = false;
            InputUtil.setKeyPressed(key, false);
            ReflectionUtils.setButton(0, false);
        }
    }

    private long nextDelay() {
        int target = Math.max(1, (int) targetCPS.getInput());
        int baseDelay = 1000 / target;

        int finalDelay;

        if (simulateExhaust.isToggled()) {
            int variation = rand.nextInt(baseDelay + 1) - (baseDelay / 2);
            finalDelay = baseDelay + variation;

            if (rand.nextInt(100) < 15) {
                if (rand.nextBoolean()) {
                    finalDelay = 25 + rand.nextInt(16);
                } else {
                    finalDelay = baseDelay + 50 + rand.nextInt(41);
                }
            }

            if (rand.nextInt(100) < 8) {
                int spikeMult = 50 + rand.nextInt(151);
                finalDelay = (finalDelay * spikeMult) / 100;
            }

            if (rand.nextInt(100) < 10) {
                finalDelay += 10 + rand.nextInt(26);
            }
        } else {
            finalDelay = baseDelay + (rand.nextInt(21) - 10);
        }

        return Math.max(33, Math.min(180, finalDelay));
    }

    private static void ensureHoveredSlotField() {
        if (hoveredSlotField != null) {
            return;
        }
        try {
            hoveredSlotField = GuiContainer.class.getDeclaredField("theSlot");
            hoveredSlotField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                hoveredSlotField = GuiContainer.class.getDeclaredField("field_147006_u");
                hoveredSlotField.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
                hoveredSlotField = null;
            }
        }
    }

    private static Slot getHoveredSlot(GuiContainer gui) {
        if (hoveredSlotField == null || gui == null) {
            return null;
        }
        try {
            Object value = hoveredSlotField.get(gui);
            if (value instanceof Slot) {
                return (Slot) value;
            }
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }
}
