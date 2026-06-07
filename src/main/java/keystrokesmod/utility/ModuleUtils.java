package keystrokesmod.utility;

import keystrokesmod.event.*;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.impl.combat.Velocity;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.impl.render.HUD;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import keystrokesmod.module.ModuleManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.play.client.*;

import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.Map;

public class ModuleUtils implements IMinecraftInstance {
    public static boolean isBreaking;
    public static boolean threwFireball, threwFireballLow;
    private int isBreakingTick;
    public static long MAX_EXPLOSION_DIST_SQ = 10;
    private long FIREBALL_TIMEOUT = 500L, fireballTime = 0;
    public static int inAirTicks, groundTicks, stillTicks;
    public static int fadeEdge;
    public static int lastFaceDifference;
    private int lastFace;
    public static double offsetValue = 0.0000000000201;
    public static boolean isAttacking;
    private int attackingTicks;
    public static int profileTicks = -1;
    public static boolean lastTickOnGround, lastTickPos1;
    private boolean thisTickOnGround, thisTickPos1;
    public static boolean firstDamage;

    public static boolean isBlocked;

    public static boolean damage;
    private int damageTicks;
    private boolean lowhopAir;
    private static boolean allowFriction;

    public static boolean canSlow, didSlow, setSlow;

    
    public void onSendPacketNoEvent(NoEventPacketEvent e) {
        handleAllPacket(e.getPacket());
    }

    
    public void onSendPacket(SendPacketEvent e) {
        handleAllPacket(e.getPacket());

        if (e.getPacket() instanceof PlayerActionC2SPacket) {
            isBreaking = true;
        }

        if (e.getPacket() instanceof PlayerInteractBlockC2SPacket && Utils.holdingFireball()) {
            if (Utils.isBindDown(mc.options.keyBindUseItem)) {
                fireballTime = System.currentTimeMillis();
                threwFireball = true;
                if (mc.player.rotationPitch > 50F) {
                    threwFireballLow = true;
                }
            }
        }

        if (e.getPacket() instanceof PlayerInteractBlockC2SPacket && Utils.scaffoldDiagonal(false)) {
            if (((PlayerInteractBlockC2SPacket) e.getPacket()).getPlacedBlockDirection() != 1) {
                int currentFace = ((PlayerInteractBlockC2SPacket) e.getPacket()).getPlacedBlockDirection();

                if (currentFace == lastFace) {
                    lastFaceDifference++;
                }
                else {
                    lastFaceDifference = 0;
                }
                lastFace = currentFace;
            }
        }
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck() || e.isCanceled()) {
            return;
        }
        if (e.getPacket() instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket s27 = (ExplosionS2CPacket) e.getPacket();
            if (threwFireball) {
                if ((mc.player.getPosition().distanceSq(s27.getX(), s27.getY(), s27.getZ()) <= MAX_EXPLOSION_DIST_SQ)) {
                    ModuleManager.velocity.disable = false;
                    ModuleManager.antiKnockback.disable = false;
                    threwFireball = false;
                    e.setCanceled(false);
                }
            }
        }
    }

    private void handleAllPacket(Packet<?> packet) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (packet instanceof PlayerInteractBlockC2SPacket && Utils.holdingSword() && !BlockUtils.isInteractable(mc.objectMouseOver) && !isBlocked) {
            isBlocked = true;
        }
        else if (packet instanceof PlayerActionC2SPacket && isBlocked) {
            if (((PlayerActionC2SPacket) packet).getStatus() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
                isBlocked = false;
            }
        }
        else if (packet instanceof C09PacketHeldItemChange && isBlocked) {
            isBlocked = false;
        }
        if (packet instanceof C02PacketUseEntity) {
            isAttacking = true;
            attackingTicks = 5;
        }
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (damage && ++damageTicks >= 8) {
            damage = firstDamage = false;
            damageTicks = 0;
        }
        profileTicks++;

        if (isAttacking) {
            if (attackingTicks <= 0) {
                isAttacking = false;
            }
            else {
                --attackingTicks;
            }
        }

        if (LongJump.slotReset && ++LongJump.slotResetTicks >= 2) {
            LongJump.stopModules = false;
            LongJump.slotResetTicks = 0;
            LongJump.slotReset = false;
        }

        if (!ModuleManager.bHop.hopping) {
            allowFriction = false;
        }
        else if (!mc.player.onGround) {
            allowFriction = true;
        }

        if (fireballTime > 0 && (System.currentTimeMillis() - fireballTime) > FIREBALL_TIMEOUT / 3) {
            threwFireballLow = false;
            ModuleManager.velocity.disable = false;
            ModuleManager.antiKnockback.disable = false;
        }

        if (fireballTime > 0 && (System.currentTimeMillis() - fireballTime) > FIREBALL_TIMEOUT) {
            threwFireball = threwFireballLow = false;
            fireballTime = 0;
            ModuleManager.velocity.disable = false;
            ModuleManager.antiKnockback.disable = false;
        }

        if (isBreaking && ++isBreakingTick >= 1) {
            isBreaking = false;
            isBreakingTick = 0;
        }
    }

    
    public void onPostMotion(PostMotionEvent e) {
        if (bHopBoostConditions()) {
            if (firstDamage) {
                Utils.setSpeed(Utils.getHorizontalSpeed());
                firstDamage = false;
            }
        }
    }

    private boolean bHopBoostConditions() {
        if (ModuleManager.bHop.isEnabled() && ModuleManager.bHop.damageBoost.isToggled() && (!ModuleManager.bHop.damageBoostRequireKey.isToggled() || ModuleManager.bHop.damageBoostKey.isPressed())) {
            return true;
        }
        return false;
    }

    public static double applyFrictionMulti() {
        final int speedAmplifier = Utils.getSpeedAmplifier();
        if (speedAmplifier > 1 && allowFriction) {
            return 1;
        }
        return 1;
    }

    
    public void onPreMotion(PreMotionEvent e) {
        int simpleY = (int) Math.round((e.posY % 1) * 10000);

        lastTickOnGround = thisTickOnGround;
        thisTickOnGround = mc.player.onGround;

        lastTickPos1 = thisTickPos1;
        thisTickPos1 = mc.player.getY() % 1 == 0;

        inAirTicks = mc.player.onGround ? 0 : ++inAirTicks;
        groundTicks = !mc.player.onGround ? 0 : ++groundTicks;
        stillTicks = Utils.isMoving() ? 0 : ++stillTicks;

        Block blockBelow = BlockUtils.getBlockState().getBlock()new BlockPos(mc.player.getX(), mc.player.getY() - 1, mc.player.getZ()));
        Block blockBelow2 = BlockUtils.getBlockState().getBlock()new BlockPos(mc.player.getX(), mc.player.getY() - 2, mc.player.getZ()));
        Block block = BlockUtils.getBlockState().getBlock()new BlockPos(mc.player.getX(), mc.player.getY(), mc.player.getZ()));

        if (ModuleManager.bHop.didMove) {

            if ((!ModuleUtils.damage || Velocity.vertical.getInput() == 0) && !mc.player.isCollidedHorizontally) {

                if (!(block instanceof BlockAir) || (blockBelow instanceof BlockAir && blockBelow2 instanceof BlockAir)) {
                    resetLowhop();
                }
                switch ((int) ModuleManager.bHop.mode.getInput()) {
                    case 2: // 9 tick
                        switch (simpleY) {
                            case 13:
                                mc.player.motionY = mc.player.motionY - 0.02483;
                                break;
                            case 2000:
                                mc.player.motionY = mc.player.motionY - 0.1913;
                                break;
                            case 7016:
                                mc.player.motionY = mc.player.motionY + 0.08;
                                break;
                        }
                        if (ModuleUtils.inAirTicks > 6 && Utils.isMoving()) {
                            Utils.setSpeed(Utils.getHorizontalSpeed(mc.player));
                        }
                        if (ModuleUtils.inAirTicks > 8) {
                            resetLowhop();
                        }
                        break;
                    case 3: // 8 tick
                        switch (simpleY) {
                            case 13:
                                mc.player.motionY = mc.player.motionY - 0.045;//0.02483;
                                break;
                            case 2000:
                                mc.player.motionY = mc.player.motionY - 0.175;//0.1913;
                                resetLowhop();
                                break;
                        }
                        break;
                    case 4: // 7 tick
                        switch (simpleY) {
                            case 4200:
                                mc.player.motionY = 0.39;
                                break;
                            case 1138:
                                mc.player.motionY = mc.player.motionY - 0.13;
                                break;
                            case 2031:
                                mc.player.motionY = mc.player.motionY - 0.2;
                                resetLowhop();
                                break;
                        }
                        break;
                }
            }
        }
        if (!mc.player.onGround) {
            lowhopAir = true;
        }
        else if (lowhopAir) {
            resetLowhop();
        }

        if (ModuleManager.bHop.setRotation) {
            if (KillAura.target == null) {
                float yaw = mc.player.rotationYaw - 55;
                e.setYaw(yaw);
            }
            if (mc.player.onGround) {
                ModuleManager.bHop.setRotation = false;
            }
        }

        if (canSlow && !mc.player.onGround) {
            double motionVal = 0.9 - ((double) inAirTicks / 10000) - Utils.randomizeDouble(0.00001, 0.00006);
            if (mc.player.hurtTime == 0 && inAirTicks > 4 && !setSlow) {
                mc.player.motionX *= motionVal;
                mc.player.motionZ *= motionVal;
                setSlow = true;
            }
            didSlow = true;
        }
        else if (didSlow) {
            canSlow = didSlow = false;
        }
        if (mc.player.onGround) {
            setSlow = false;
        }
    }

    private void resetLowhop() {
        ModuleManager.bHop.lowhop = false;
        ModuleManager.bHop.didMove = false;
        lowhopAir = false;
    }

    public static void handleSlow() {
        didSlow = false;
        canSlow = true;
    }

    
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (ModuleManager.killAura.rotationMode.getInput() == 0 && KillAura.target != null) {
            mc.player.prevRenderArmYaw = mc.player.rotationYaw;
            mc.player.renderArmYaw = mc.player.rotationYaw;
        }
        // Scaffold fading highlight removed.
    }
}