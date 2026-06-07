package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.AttackEvent;
import keystrokesmod.event.GameTickEvent;
import keystrokesmod.event.PrePlayerInteractEvent;
import keystrokesmod.lag.api.EnumLagDirection;
import keystrokesmod.lag.api.LagRequest;
import keystrokesmod.lag.timeout.ModuleBackedTimeout;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.CombatTargeting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3dd;

import org.lwjgl.opengl.GL11;

public class LagRange extends Module {
    private static final double MINIMUM_DISTANCE_SQ = 3.0 * 3.0;
    private static final long INDICATOR_INTERP_MS = 80L;
    private static final double POS_EPS = 1.0e-6;

    private final SliderSetting range;
    private final SliderSetting maximumDelay;
    private final ButtonSetting sprintReset;
    private final ButtonSetting blockSword;
    private final ButtonSetting usedSplashPotion;
    private final ButtonSetting holdingWeapon;
    private final ButtonSetting realPositionIndicator;
    private final ButtonSetting showInFirstPerson;
    private final ColorSetting indicatorColor;
    private final SliderSetting indicatorLineWidth;
    private final ButtonSetting indicatorFilled;

    private PlayerEntity currentTarget;
    private double lastDistSq = -1;
    private boolean isLagging;
    private int lastSelfHurtTime;
    private int lastTargetHurtTime;
    private int hitMarkedEntityId;
    private boolean lastSprintState;
    private boolean lastBlockingState;
    private LagRequest outboundLag;

    private Vec3d indicatorInterpFrom;
    private Vec3d indicatorInterpTo;
    private long indicatorInterpStartMs;

    public LagRange() {
        super("Lag Range", category.combat);
        this.registerSetting(range = new SliderSetting("Range", 6.0, 3.0, 10.0, 0.1));
        this.registerSetting(maximumDelay = new SliderSetting("Maximum delay", "ms", 200, 50, 1000, 10));
        this.registerSetting(new DescriptionSetting("Flush conditions"));
        this.registerSetting(sprintReset = new ButtonSetting("Sprint reset", true));
        this.registerSetting(blockSword = new ButtonSetting("Block sword", true));
        this.registerSetting(usedSplashPotion = new ButtonSetting("Used splash potion", true));
        this.registerSetting(new DescriptionSetting("Indicator"));
        this.registerSetting(realPositionIndicator = new ButtonSetting("Real position indicator", true));
        this.registerSetting(showInFirstPerson = new ButtonSetting("Show in first person", false));
        this.registerSetting(indicatorColor = new ColorSetting("Indicator color", 255, 0, 0, 100));
        this.registerSetting(indicatorLineWidth = new SliderSetting("Indicator line width", 2.0, 1.0, 5.0, 0.5));
        this.registerSetting(indicatorFilled = new ButtonSetting("Indicator filled", false));
        this.registerSetting(new DescriptionSetting("Conditions"));
        this.registerSetting(holdingWeapon = new ButtonSetting("Holding a weapon", true));
        this.closetModule = true;
    }

    @Override
    public void onEnable() {
        resetState();
    }

    @Override
    public void onDisable() {
        flushLag();
        resetState();
    }

    @Override
    public String getInfo() {
        return (int) maximumDelay.getInput() + "ms";
    }

    
    public void onPrePlayerInteract(PrePlayerInteractEvent e) {
        if (!Utils.nullCheck() || mc.player.isDead || mc.world == null) {
            if (isLagging) flushLag();
            resetState();
            return;
        }

        if (ModuleManager.bedAura != null && ModuleManager.bedAura.isActivelyMining()) {
            if (isLagging) flushLag();
            return;
        }

        Autoblock autoblock = (Autoblock) ModuleManager.getModule(Autoblock.class);
        if (autoblock != null && autoblock.isActive()) {
            if (isLagging) flushLag();
            return;
        }

        double rangeSq = range.getInput() * range.getInput();
        boolean moving = isMoving();

        PlayerEntity nextTarget = CombatTargeting.findTarget(rangeSq);
        if (!sameTarget(nextTarget)) {
            if (isLagging) flushLag();
            lastDistSq = -1;
            hitMarkedEntityId = -1;
            lastTargetHurtTime = nextTarget != null ? nextTarget.hurtTime : 0;
        }
        currentTarget = nextTarget;

        if (currentTarget != null) {
            double distSq = RotationUtils.distanceSqFromEyeToClosestOnAABB(currentTarget);

            if (isLagging) {
                if (distSq > rangeSq) {
                    flushLag();
                    lastDistSq = distSq;
                    hitMarkedEntityId = -1;
                    lastTargetHurtTime = currentTarget.hurtTime;
                    return;
                }

                if (lastDistSq >= 0 && distSq >= lastDistSq) {
                    boolean hitHold = hitMarkedEntityId == currentTarget.getEntityId()
                            && distSq <= MINIMUM_DISTANCE_SQ
                            && mc.player.hurtTime == 0;
                    if (!hitHold) {
                        flushLag();
                        lastDistSq = distSq;
                        lastTargetHurtTime = currentTarget.hurtTime;
                        return;
                    }
                }

                int hurtTime = mc.player.hurtTime;
                if (hurtTime > lastSelfHurtTime) {
                    flushLag();
                    hitMarkedEntityId = -1;
                    lastSelfHurtTime = hurtTime;
                    lastDistSq = distSq;
                    lastTargetHurtTime = currentTarget.hurtTime;
                    return;
                }
                lastSelfHurtTime = hurtTime;

                Raven.lagHandler.releaseExpiredPackets(EnumLagDirection.OUTBOUND, (long) maximumDelay.getInput());

                if (holdingWeapon.isToggled() && !Utils.holdingWeapon()) {
                    flushLag();
                    lastDistSq = distSq;
                    lastTargetHurtTime = currentTarget.hurtTime;
                    return;
                }

                if (sprintReset.isToggled()) {
                    boolean sprintingNow = mc.player.isSprinting();
                    if (sprintingNow && !lastSprintState) {
                        flushLag();
                        lastSprintState = sprintingNow;
                        lastDistSq = distSq;
                        lastTargetHurtTime = currentTarget.hurtTime;
                        return;
                    }
                    lastSprintState = sprintingNow;
                }

                if (blockSword.isToggled()) {
                    boolean blockingNow = mc.player.isBlocking();
                    if (blockingNow && !lastBlockingState) {
                        flushLag();
                        lastBlockingState = blockingNow;
                        lastDistSq = distSq;
                        lastTargetHurtTime = currentTarget.hurtTime;
                        return;
                    }
                    lastBlockingState = blockingNow;
                }

                if (usedSplashPotion.isToggled() && mc.player.isUsingItem()) {
                    ItemStack held = mc.player.getHeldItem();
                    if (held != null && held.getItem() instanceof ItemPotion && ItemPotion.isSplash(held.getMetadata())) {
                        flushLag();
                        lastDistSq = distSq;
                        lastTargetHurtTime = currentTarget.hurtTime;
                        return;
                    }
                }

                lastDistSq = distSq;
                lastTargetHurtTime = currentTarget.hurtTime;
                return;
            }

            int hurtTime = mc.player.hurtTime;
            if (hurtTime > lastSelfHurtTime) {
                hitMarkedEntityId = -1;
            }
            lastSelfHurtTime = hurtTime;
            lastSprintState = mc.player.isSprinting();
            lastBlockingState = mc.player.isBlocking();

            if (hurtTime == 0
                    && lastTargetHurtTime == 0
                    && currentTarget.hurtTime > 0) {
                hitMarkedEntityId = currentTarget.getEntityId();
            }
            lastTargetHurtTime = currentTarget.hurtTime;

            boolean closing = lastDistSq >= 0 && distSq < lastDistSq;
            boolean outsideMinDist = distSq > MINIMUM_DISTANCE_SQ;
            boolean weaponOk = !holdingWeapon.isToggled() || Utils.holdingWeapon();
            boolean hitMarkedHere = hitMarkedEntityId == currentTarget.getEntityId();
            boolean hitStart = hitMarkedHere && distSq <= MINIMUM_DISTANCE_SQ && hurtTime == 0 && moving && weaponOk;

            lastDistSq = distSq;

            if (hurtTime == 0 && weaponOk && moving
                    && (closing && outsideMinDist || hitStart) {
                startLag();
            }
        } else {
            if (isLagging) flushLag();
            lastDistSq = -1;
            hitMarkedEntityId = -1;
            lastTargetHurtTime = 0;
        }
    }

    
    public void onAttackEvent(AttackEvent e) {
        if (isLagging && e.attacker == mc.player) {
            flushLag();
        }
    }

    
    public void onGameTick(GameTickEvent e) {
        if (mc.currentScreen == null) {
            return;
        }

        if (isLagging) {
            flushLag();
        }
        resetState();
    }

    
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!Utils.nullCheck()) return;
        if (!isLagging) {
            clearIndicatorInterp();
            return;
        }
        if (!realPositionIndicator.isToggled()) return;
        if (mc.options.thirdPersonView == 0 && !showInFirstPerson.isToggled()) return;

        Vec3d delayedPos = Raven.lagHandler.getLastReleasedServerPosition();
        if (delayedPos == null) {
            clearIndicatorInterp();
            return;
        }

        long nowMs = System.currentTimeMillis();
        if (indicatorInterpTo == null) {
            indicatorInterpFrom = delayedPos;
            indicatorInterpTo = delayedPos;
            indicatorInterpStartMs = nowMs;
        } else if (serverPosChanged(delayedPos, indicatorInterpTo)) {
            double te = Math.min(1.0D, (nowMs - indicatorInterpStartMs) / (double) INDICATOR_INTERP_MS);
            indicatorInterpFrom = lerpVec3d(indicatorInterpFrom, indicatorInterpTo, te);
            indicatorInterpTo = delayedPos;
            indicatorInterpStartMs = nowMs;
        }

        double t = Math.min(1.0D, (nowMs - indicatorInterpStartMs) / (double) INDICATOR_INTERP_MS);
        Vec3d drawPos = lerpVec3d(indicatorInterpFrom, indicatorInterpTo, t);

        double viewX = mc.getEntityRenderDispatcher().viewerPosX;
        double viewY = mc.getEntityRenderDispatcher().viewerPosY;
        double viewZ = mc.getEntityRenderDispatcher().viewerPosZ;

        float halfW = mc.player.width / 2.0f;
        float height = mc.player.height;
        Box worldBox = new Box(
                drawPos.xCoord - halfW, drawPos.yCoord, drawPos.zCoord - halfW,
                drawPos.xCoord + halfW, drawPos.yCoord + height, drawPos.zCoord + halfW
        );
        Vec3d cameraPos = Utils.getCameraPos(e.partialTicks);
        if (worldBox.isVecInside(cameraPos)) return;
        Box box = worldBox.offset(-viewX, -viewY, -viewZ);

        float r = indicatorColor.getRed() / 255.0f;
        float g = indicatorColor.getGreen() / 255.0f;
        float b = indicatorColor.getBlue() / 255.0f;
        float a = indicatorColor.getAlpha() / 255.0f;

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture2D();
        RenderSystem.disableDepth();
        RenderSystem.depthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (indicatorFilled.isToggled()) {
            RenderUtils.drawBoundingBox(box, r, g, b, a);
        }

        RenderSystem.lineWidth((float) indicatorLineWidth.getInput());
        RenderSystem.setShaderColor(r, g, b, a);
        BufferBuilder.drawSelectionBoundingBox(box);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepth();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture2D();
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().popMatrix();
    }

    private void startLag() {
        outboundLag = new LagRequest(EnumLagDirection.ONLY_OUTBOUND, new ModuleBackedTimeout(this));
        Raven.lagHandler.requestLag(outboundLag);
        isLagging = true;
    }

    private void flushLag() {
        if (!isLagging) return;
        if (outboundLag != null) {
            outboundLag.getTimeout().forceTimeOut();
            outboundLag = null;
        }
        isLagging = false;
        clearIndicatorInterp();
    }

    private void resetState() {
        currentTarget = null;
        lastDistSq = -1;
        isLagging = false;
        lastSelfHurtTime = 0;
        lastTargetHurtTime = 0;
        hitMarkedEntityId = -1;
        lastSprintState = false;
        lastBlockingState = false;
        outboundLag = null;
        clearIndicatorInterp();
    }

    private void clearIndicatorInterp() {
        indicatorInterpFrom = null;
        indicatorInterpTo = null;
        indicatorInterpStartMs = 0L;
    }

    private static boolean serverPosChanged(Vec3d a, Vec3d b) {
        return Math.abs(a.xCoord - b.xCoord) > POS_EPS
                || Math.abs(a.yCoord - b.yCoord) > POS_EPS
                || Math.abs(a.zCoord - b.zCoord) > POS_EPS;
    }

    private static Vec3d lerpVec3d(Vec3d from, Vec3d to, double t) {
        if (t <= 0.0D) {
            return from;
        }
        if (t >= 1.0D) {
            return to;
        }
        return new Vec3d(
                from.xCoord + (to.xCoord - from.xCoord) * t,
                from.yCoord + (to.yCoord - from.yCoord) * t,
                from.zCoord + (to.zCoord - from.zCoord) * t
        );
    }

    private boolean sameTarget(PlayerEntity nextTarget) {
        if (currentTarget == null || nextTarget == null) {
            return currentTarget == nextTarget;
        }
        return currentTarget.getEntityId() == nextTarget.getEntityId();
    }

    private boolean isMoving() {
        return mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f;
    }
}
