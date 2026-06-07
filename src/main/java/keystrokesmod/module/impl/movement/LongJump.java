package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.event.*;
// import IMixinItemRenderer removed
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3dd;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.ModuleUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

// TODO: Remove Forge packet imports

import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;

public class LongJump extends Module {
    private SliderSetting mode;

    private SliderSetting boostSetting;
    private SliderSetting verticalMotion;
    private SliderSetting motionDecay;

    private ButtonSetting manual;
    private ButtonSetting onlyWithVelocity;
    private KeySetting disableKey;

    private ButtonSetting allowStrafe;
    private ButtonSetting invertYaw;
    private ButtonSetting stopMovement;
    private ButtonSetting hideExplosion;
    public ButtonSetting spoofItem;

    private KeySetting temporaryFlightKey;

    public String[] modes = new String[]{"Float", "Boost"};

    private boolean manualWasOn;

    private float yaw;
    private float pitch;

    private boolean notMoving;
    private boolean enabled;
    public static boolean function;
    private Double startY;

    private int boostTicks;
    public int lastSlot = -1;
    private int stopTime;
    private int rotateTick;
    private int motionDecayVal;

    private long fireballTime;
    private long MAX_EXPLOSION_DIST_SQ = 9;
    private long FIREBALL_TIMEOUT = 750L;

    public static boolean stopVelocity;
    public static boolean stopModules;
    public static boolean slotReset;
    public static int slotResetTicks;

    private int firstSlot = -1;

    public LongJump() {
        super("Long Jump", category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", 0, modes));

        this.registerSetting(manual = new ButtonSetting("Manual", false));
        this.registerSetting(onlyWithVelocity = new ButtonSetting("Only while velocity enabled", false));
        this.registerSetting(disableKey = new KeySetting("Disable key", GLFW.GLFW_KEY_SPACE));

        this.registerSetting(boostSetting = new SliderSetting("Horizontal boost", 1.7, 0.0, 2.0, 0.05));
        this.registerSetting(verticalMotion = new SliderSetting("Vertical motion", 0, 0.4, 0.9, 0.01));
        this.registerSetting(motionDecay = new SliderSetting("Motion decay", 17, 1, 40, 1));
        this.registerSetting(allowStrafe = new ButtonSetting("Allow strafe", false));
        this.registerSetting(invertYaw = new ButtonSetting("Invert yaw", true));
        this.registerSetting(stopMovement = new ButtonSetting("Stop movement", false));
        this.registerSetting(hideExplosion = new ButtonSetting("Hide explosion", false));
        this.registerSetting(spoofItem = new ButtonSetting("Spoof item", false));

        this.registerSetting(temporaryFlightKey = new KeySetting("Vertical key", GLFW.GLFW_KEY_SPACE));
    }

    @Override
    public void guiUpdate() {
        this.onlyWithVelocity.setVisible(manual.isToggled(), this);
        this.disableKey.setVisible(manual.isToggled(), this);

        this.verticalMotion.setVisible(mode.getInput() == 0, this);
        this.motionDecay.setVisible(mode.getInput() == 0, this);
        this.temporaryFlightKey.setVisible(mode.getInput() == 0, this);
    }

    public void onEnable() {
        if (!manual.isToggled()) {
            if (Utils.getTotalHealth(mc.player) <= 3) {
                Utils.sendMessage("&cPrevented throwing fireball due to low health");
                disable();
                return;
            }
            enabled();
        }
    }

    private double vec3d_x, vec3d_y, vec3d_z;

    public void onDisable() {
        disabled();
    }

    
    public void onCollision(CollisionEvent event) {
        if (this.startY != null && !manual.isToggled() && boostTicks <= 0) {
            BlockPos blockPos = event.pos;
            if (event.state.isSolidBlock(mc.world, event.pos) && blockPos.getY() <= this.startY) {
                event.boundingBox = new Box(blockPos.getX() - 2.0D, blockPos.getY() - 1.0D, blockPos.getZ() - 2.0D, blockPos.getX() + 2.0D, blockPos.getY() + 1.0D, blockPos.getZ() + 2.0D);
            }
        }
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (manual.isToggled()) {
            manualWasOn = true;
        }
        else {
            if (manualWasOn) {
                disabled();
            }
            manualWasOn = false;
        }

        if (manual.isToggled() && disableKey.isPressed() && Utils.jumpDown()) {
            function = false;
            disabled();
        }

        if (!function) {
            if (manual.isToggled() && !enabled) {
                if (ModuleUtils.threwFireballLow) {
                    ModuleManager.velocity.disable = true;
                    // ModuleManager.antiKnockback.disable = true; // TODO: needs package access
                    enabled();
                }
            }
            return;
        }

        if (spoofItem.isToggled()) {
            // TODO: IMixinItemRenderer not available in 1.21.4
            // TODO: IMixinItemRenderer not available in 1.21.4
        }

        if (enabled) {
            if (!Utils.isMoving()) notMoving = true;
            if (boostSetting.getInput() == 0 && verticalMotion.getInput() == 0) {
                Utils.sendMessage("&cValues are set to 0!");
                disabled();
                return;
            }
            int fireballSlot = setupFireballSlot(true);
            if (fireballSlot != -1) {
                if (!manual.isToggled()) {
                    lastSlot = mc.player.getInventory().selectedSlot;
                    if (mc.player.getInventory().selectedSlot != fireballSlot) {
                        mc.player.getInventory().selectedSlot = fireballSlot;
                    }

                }
                //("Set fireball slot");
                rotateTick = 1;
                if (stopMovement.isToggled()) {
                    stopTime = 1;
                }
            } // auto disables if -1
            enabled = false;
        }

        if (notMoving) {
            motionDecayVal = 21;
        } else {
            motionDecayVal = (int) motionDecay.getInput();
        }
        if (stopTime == -1 && ++boostTicks > (!temporaryFlightKey() ? 33/*flat motion ticks*/ : (!notMoving ? 32/*normal motion ticks*/ : 33/*vertical motion ticks*/))) {
            disabled();
            return;
        }

        if (fireballTime > 0 && (System.currentTimeMillis() - fireballTime) > FIREBALL_TIMEOUT) {
            Utils.sendMessage("&cFireball timed out.");
            disabled();
            return;
        }
        if (boostTicks > 0) {
            if (mode.getInput() == 0) {
                modifyVertical(); // has to be onPreUpdate
            }
            //Utils.sendMessage("Modifying vertical");
            if (allowStrafe.isToggled() && boostTicks < 32) {
                Utils.setSpeed(Utils.getHorizontalSpeed(mc.player));
                //Utils.sendMessage("Speed");
            }
        }

        if (stopMovement.isToggled() && !notMoving) {
            if (stopTime > 0) {
                ++stopTime;
            }
        }

        if (mc.player.isOnGround() && boostTicks > 2) {
            disabled();
        }

        if (firstSlot != -1) {
            mc.player.getInventory().selectedSlot = firstSlot;
        }
    }public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (rotateTick == 1) {
            if ((invertYaw.isToggled() || stopMovement.isToggled()) && !notMoving) {
                if (!stopMovement.isToggled()) {
                    yaw = mc.player.getYaw() - 180f;
                    pitch = 90f;
                } else {
                    yaw = mc.player.getYaw() - 180f;
                    pitch = 66.3f;//(float) pitchVal.getInput();
                }
            } else {
                yaw = mc.player.getYaw();
                pitch = 90f;
            }
            e.setRotations(yaw, pitch);
        }
        if (rotateTick > 0 && ++rotateTick >= 3) {
            rotateTick = 0;
            int fireballSlot = setupFireballSlot(false);
            if (fireballSlot != -1) {
                fireballTime = System.currentTimeMillis();
                if (!manual.isToggled()) {
                    // PlayerInteractItemC2SPacket needs PacketByteBuf in 1.21.4
                    //((IAccessorMinecraft) mc).callRightClickMouse();
                }
                mc.player.swingHand(Hand.MAIN_HAND);
                // mc.getItemRenderer().resetEquippedProgress(); // removed in 1.21.4
                stopVelocity = true;
                //Utils.sendMessage("Right click");
            }
        }
        if (boostTicks == 1) {
            if (invertYaw.isToggled()) {
                //client.setMotion(client.getMotion().x, client.getMotion().y + 0.035d, client.getMotion().z);
            }
            modifyHorizontal();
            stopVelocity = false;
            if (!manual.isToggled() && !allowStrafe.isToggled() && mode.getInput() == 1) {
                disabled();
            }
        }

    }
    public void onMoveInput(PrePlayerInputEvent e) {
        if (!function) {
            return;
        }
        // mc.player.input.jumping = false; // TODO: verify field name
        if (rotateTick > 0 || fireballTime > 0) {
            if (Utils.isMoving()) e.setForward(1);
            e.setStrafe(0);
        }
        if (notMoving && boostTicks < 3) {
            e.setForward(0);
            e.setStrafe(0);
            Utils.setSpeed(0);
        }
        if (stopMovement.isToggled() && !notMoving && stopTime >= 1) {
            e.setForward(0);
            e.setStrafe(0);
            Utils.setSpeed(0);
        }
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!function) {
            return;
        }
        Object packet = e.getPacket();
        if (packet instanceof net.minecraft.network.packet.s2c.play.ExplosionS2CPacket) {
            net.minecraft.network.packet.s2c.play.ExplosionS2CPacket s27 = (net.minecraft.network.packet.s2c.play.ExplosionS2CPacket) packet;
            if (fireballTime == 0 || mc.player.getPos().squaredDistanceTo((double)(double)0 /* getPlayerKnockback disabled */, (double)(double)0, (double)(double)0) > MAX_EXPLOSION_DIST_SQ) {
                e.setCanceled(true);
                //Utils.sendMessage("0 fb time / out of dist");
            }

            stopTime = -1;
            fireballTime = 0;
            resetSlot();
            boostTicks = 0; // +1 on next pre update
            //Utils.sendMessage("set start vals");

            //client.print(client.getPlayer().getTicksExisted() + " s27 " + boostTicks + " " + client.getPlayer().getHurtTime() + " " + client.getPlayer().getSpeed());
        } else if (packet instanceof net.minecraft.network.packet.s2c.play.ExplosionS2CPacket) {
            Utils.sendMessage("&cReceived setback, disabling.");
            disabled();
        }

        if (hideExplosion.isToggled() && fireballTime != 0 && (packet instanceof Object || packet instanceof Object || packet instanceof Object)) {
            e.setCanceled(true);
        }
    }

    private int getFireballSlot() {
        int n = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack getStackInSlot = mc.player.getInventory().getStack(i);
            if (getStackInSlot != null && getStackInSlot.getItem() == Items.FIRE_CHARGE) {
                n = i;
                break;
            }
        }
        return n;
    }

    private void enabled() {
        slotReset = false;
        slotResetTicks = 0;
        enabled = function = true;

        stopModules = true;
        startY = mc.player.getY();
    }

    private void disabled() {
        fireballTime = rotateTick = stopTime = 0;
        boostTicks = -1;
        resetSlot();
        enabled = function = notMoving = stopVelocity = stopModules = false;
        if (!manual.isToggled()) {
            disable();
        }
        startY = null;
    }

    private int setupFireballSlot(boolean pre) {
        // only cancel bad packet right click on the tick we are sending it
        int fireballSlot = getFireballSlot();
        if (fireballSlot == -1) {
            Utils.sendMessage("&cFireball not found.");
            disabled();
        } else if (pre && Utils.distanceToGround(mc.player) > 3/* || (!pre && !PacketUtil.canRightClickItem())*/) { //needs porting
            Utils.sendMessage("&cCan't throw fireball right now.");
            disabled();
            fireballSlot = -1;
        }
        return fireballSlot;
    }

    private void resetSlot() {
        if (lastSlot != -1 && !manual.isToggled()) {
            if (spoofItem.isToggled()) {
                // TODO: IMixinItemRenderer not available in 1.21.4
                // TODO: IMixinItemRenderer not available in 1.21.4
            }
            mc.player.getInventory().selectedSlot = lastSlot;
            lastSlot = -1;
            firstSlot = -1;
        }
        slotReset = true;
    }

    private int getSpeedLevel() {
        for (net.minecraft.entity.effect.StatusEffectInstance potionEffect : mc.player.getStatusEffects()) {
            if (potionEffect.getTranslationKey().contains("speed")) {
                return potionEffect.getAmplifier() + 1;
            }
            return 0;
        }
        return 0;
    }

    // only apply horizontal boost once
    void modifyHorizontal() {
        if (boostSetting.getInput() != 0) {
            //client.print("&7horizontal &b" + boostTicks + " " + client.getPlayer().getHurtTime());

            double speed = boostSetting.getInput() - Utils.randomizeDouble(0.0001, 0);
            if (Utils.isMoving()) {
                Utils.setSpeed(speed);
                //Utils.sendMessage("og speed");
            }
        }
    }

    private void modifyVertical() {
        if (verticalMotion.getInput() != 0) {
            double ver = ((!notMoving ? verticalMotion.getInput() : 1.16 /*vertical*/) * (1.0 / (1.0 + (0.05 * getSpeedLevel())))) + Utils.randomizeDouble(0.0001, 0.1);
            double decay = motionDecay.getInput() / 1000;
            if (boostTicks > 1 && !temporaryFlightKey()) {
                if (boostTicks > 1 || boostTicks <= (!notMoving ? 32/*horizontal motion ticks*/ : 33/*vertical motion ticks*/)) {
                    vec3d_y = Utils.randomizeDouble(0.0101, 0.01);
                }
            } else {
                if (boostTicks >= 1 && boostTicks <= (!notMoving ? 32/*horizontal motion ticks*/ : 33/*vertical motion ticks*/)) {
                    vec3d_y = ver - boostTicks * decay;
                } else if (boostTicks >= (!notMoving ? 32/*horizontal motion ticks*/ : 33/*vertical motion ticks*/) + 3) {
                    vec3d_y = mc.player.getVelocity().y + 0.028;
                    Utils.sendMessage("?");
                }
            }
        }
    }

    private boolean temporaryFlightKey() {
        if (notMoving) return true;
        return temporaryFlightKey.isPressed();
    }
}