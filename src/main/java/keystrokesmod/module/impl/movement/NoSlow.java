package keystrokesmod.module.impl.movement;

import keystrokesmod.Raven;
import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.BowItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class NoSlow extends Module {
    public static SliderSetting mode;
    public static SliderSetting slowed;
    public static ButtonSetting disableBow;
    public static ButtonSetting disablePotions;
    public static ButtonSetting swordOnly;
    public static ButtonSetting vanillaSword;

    private final String[] NOSLOW_MODES = new String[]{"Vanilla", "Beta"};
    public boolean noSlowing;

    public NoSlow() {
        super("NoSlow", category.movement, 0);
        this.registerSetting(new DescriptionSetting("Default is 80% motion reduction."));
        this.registerSetting(mode = new SliderSetting("Mode", 0, NOSLOW_MODES));
        this.registerSetting(slowed = new SliderSetting("Slow %", 80.0D, 0.0D, 80.0D, 1.0D));
        this.registerSetting(disableBow = new ButtonSetting("Disable bow", false));
        this.registerSetting(disablePotions = new ButtonSetting("Disable potions", false));
        this.registerSetting(swordOnly = new ButtonSetting("Sword only", false));
        this.registerSetting(vanillaSword = new ButtonSetting("Vanilla sword", false));
    }

    @Override
    public void onEnable() { Raven.EVENT_BUS.subscribe(this); }

    @Override
    public void onDisable() {
        noSlowing = false;
        Raven.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.player == null) return;
        if (vanillaSword.isToggled() && Utils.holdingSword()) return;
        boolean apply = getSlowed() != 0.2f;
        if (!apply || !mc.player.isUsingItem()) return;

        if ((int) mode.getInput() == 1) { // Beta
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
        }
    }

    @EventHandler
    public void onPostPlayerInput(PostPlayerInputEvent e) {
        if (noSlowing && mc.player != null)) {
            mc.player.input.jumping = true;
            noSlowing = false;
        }
    }

    public static float getSlowed() {
        if (mc.player == null) return 0.2f;
        if (mc.player.getMainHandStack().isEmpty() || ModuleManager.noSlow == null || !ModuleManager.noSlow.isEnabled()) return 0.2f;
        if (swordOnly.isToggled() && !(mc.player.getMainHandStack().getItem() instanceof SwordItem)) return 0.2f;
        if (mc.player.getMainHandStack().getItem() instanceof BowItem && disableBow.isToggled()) return 0.2f;
        if (mc.player.getMainHandStack().getItem() instanceof PotionItem && disablePotions.isToggled()) return 0.2f;
        return (100.0F - (float) slowed.getInput()) / 100.0F;
    }

    @Override
    public String getInfo() { return NOSLOW_MODES[(int) mode.getInput()]; }
}