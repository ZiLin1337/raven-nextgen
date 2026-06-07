package keystrokesmod.module.impl.player;

import keystrokesmod.Raven;
import keystrokesmod.event.ClientRotationEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.HitResult;

public class WaterBucket extends Module {
    public ButtonSetting pickupWater;
    public ButtonSetting silentAim;
    public ButtonSetting switchToItem;

    private final long PLACE_DELAY = 500L;
    private final long PICKUP_WAIT = 150L;

    private long lastPlace = 0L;
    private boolean shouldPickup = false;
    private int lastSlot = -1;

    public WaterBucket() {
        super("Water Bucket", category.player);
        this.registerSetting(pickupWater = new ButtonSetting("Pickup water", true));
        this.registerSetting(silentAim = new ButtonSetting("Silent aim", true));
        this.registerSetting(switchToItem = new ButtonSetting("Switch to item", true));
    }

    @Override
    public void onDisable() {
        this.lastPlace = 0L;
        this.shouldPickup = false;
        this.lastSlot = -1;
    }

    
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!Utils.nullCheck() || mc.isGamePaused() || mc.player.capabilities.isFlying || mc.player.capabilities.isCreativeMode) {
            return;
        }
        if (!fallCheck() {
            return;
        }
        HitResult mop = Utils.getTarget(mc.interactionManager.getBlockReachDistance(), mc.player.rotationYaw, silentAim.isToggled() ? 90.0f : mc.player.rotationPitch);
        if (mop == null || mop.typeOfHit != HitResult.MovingObjectType.BLOCK || mop.sideHit != Direction.UP) {
            return;
        }
        long now = System.currentTimeMillis();
        if (Utils.timeBetween(lastPlace, now) < PLACE_DELAY) {
            return;
        }
        if (!isItem(mc.player.getHeldItem(), Items.water_bucket) && switchToItem.isToggled() {
            this.attemptSwitch();
        }
        if (!silentAim.isToggled() && mc.player.rotationPitch < 80.0f) {
            return;
        }
        lastPlace = now;
        this.useCurrentItem();
        if (!(shouldPickup = pickupWater.isToggled()) {
            this.lastSlot = -1;
        }
        if (Raven.DEBUG) {
            Utils.sendModuleMessage(this, "&7Placed with motionY &d" + Utils.round(mc.player.motionY, 2) + " &7and fall distance &d" + Utils.round(mc.player.fallDistance, 2));
        }
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.isGamePaused() {
            return;
        }
        if (shouldPickup && Utils.timeBetween(lastPlace, System.currentTimeMillis()) > PICKUP_WAIT && isItem(mc.player.getHeldItem(), Items.bucket) {
            shouldPickup = false;
            this.useCurrentItem();
            if (this.lastSlot != -1) {
                Utils.switchSlot(this.lastSlot, true);
                this.lastSlot = -1;
            }
        }
    }

    
    public void onClientRotation(ClientRotationEvent e) {
        if (ModuleManager.bedAura != null && ModuleManager.bedAura.shouldOverrideMouseOver() {
            return;
        }
        if (silentAim.isToggled() && (fallCheck() || Utils.timeBetween(lastPlace, System.currentTimeMillis()) < PLACE_DELAY) && getWaterBucketSlot() != -1) {
            e.setYaw(mc.player.rotationYaw);
            e.setPitch(90.0f);
        }
    }

    private void attemptSwitch() {
        int slot = getWaterBucketSlot();
        if (slot != -1) {
            this.lastSlot = mc.player.inventory.currentItem;
            Utils.switchSlot(slot, true);
        }
    }

    private int getWaterBucketSlot() {
        for (int slot = 0; slot < InventoryPlayer.getHotbarSize(); ++slot) {
            if (isItem(mc.player.inventory.getStackInSlot(slot), Items.water_bucket) {
                return slot;
            }
        }
        return -1;
    }

    private void useCurrentItem() {
        mc.getNetHandler().addToSendQueue(new PlayerInteractBlockC2SPacket(mc.player.getHeldItem()));
    }

    private boolean isItem(ItemStack itemStack, Item item) {
        return itemStack != null && itemStack.getItem() == item;
    }

    private boolean fallCheck() {
        return !mc.player.onGround && mc.player.fallDistance >= 3.3;
    }
}