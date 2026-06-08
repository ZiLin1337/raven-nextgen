package keystrokesmod.module.impl.player;

import keystrokesmod.event.ClientRotationEvent;
import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.event.PrePlayerInteractEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.ReflectionUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AntiFireball extends Module {
    private final SliderSetting fov;
    private final SliderSetting range;
    private final SliderSetting targetCPS;
    private final SliderSetting rotationSpeed;
    private final ButtonSetting onGround;
    private final ButtonSetting sneakWhileActive;

    public ExplosiveProjectileEntity fireball;
    private final HashSet<Entity> fireballs = new HashSet<>();

    private long nextClickTime;
    private final Random rand = new Random();

    public AntiFireball() {
        super("Anti Fireball", category.player);
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(range = new SliderSetting("Range", 8.0, 3.0, 15.0, 0.5));
        this.registerSetting(targetCPS = new SliderSetting("Target CPS", 12.0, 1.0, 20.0, 0.5));
        this.registerSetting(rotationSpeed = new SliderSetting("Rotation speed", 15, 1, 30, 1));
        this.registerSetting(onGround = new ButtonSetting("On ground", false));
        this.registerSetting(sneakWhileActive = new ButtonSetting("Sneak while active", false));
    }

    @Override
    public void onDisable() {
        fireballs.clear();
        fireball = null;
        nextClickTime = 0;
    }

    @Override
    public void onUpdate() {
        if (!Utils.nullCheck() || mc.currentScreen != null) {
            fireball = null;
            return;
        }
        fireball = getFireball();
    }

    
    public void onPrePlayerInput(PrePlayerInputEvent e) {
        if (!Utils.nullCheck()) return;
        if (fireball != null && sneakWhileActive.isToggled() && !mc.player.isRiding()
                && !mc.player.capabilities.isFlying) {
            e.setSneak(true);
        }
    }

    
    public void onClientRotation(ClientRotationEvent e) {
        if (!Utils.nullCheck() || mc.currentScreen != null) return;
        if (ModuleManager.bedAura != null && ModuleManager.bedAura.shouldOverrideMouseOver()) {
            return;
        }
        if (onGround.isToggled() && !mc.player.onGround) return;
        if (fireball == null) return;

        float baseYaw = e.yaw != null ? e.yaw : RotationUtils.serverRotations[0];
        float basePitch = e.pitch != null ? e.pitch : RotationUtils.serverRotations[1];

        float[] target = computeAimRotations(baseYaw, basePitch);
        if (target == null) return;

        float[] smooth = RotationUtils.smoothRotation(baseYaw, basePitch, target[0], target[1],
                (int) rotationSpeed.getInput());

        e.setYaw(smooth[0]);
        e.setPitch(smooth[1]);
    }

    private float[] computeAimRotations(float baseYaw, float basePitch) {
        Vec3d eye = mc.player.getPositionEyes(1.0f);
        float borderSize = fireball.getCollisionBorderSize();
        Box fireballBox = fireball.getEntityBoundingBox().expand(borderSize, borderSize, borderSize);
        double reach = mc.interactionManager.getBlockReachDistance();

        List<PlayerEntity> players = new ArrayList<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.deathTime != 0) continue;
            if (Utils.isFriended(player)) continue;
            if (Utils.isTeammate(player)) continue;
            if (AntiBot.isBot(player)) continue;
            players.add(player);
        }
        players.sort(Comparator.comparingDouble(p -> mc.player.getDistanceSqToEntity(p)));

        for (PlayerEntity player : players) {
            float[] rot = RotationUtils.getRotationsToPoint(player.posX, player.posY, player.posZ, baseYaw, basePitch);
            if (hitsFireballBox(eye, rot[0], rot[1], fireballBox, reach)) {
                return rot;
            }
        }

        double topY = fireballBox.maxY;
        double centerX = (fireballBox.minX + fireballBox.maxX) / 2.0;
        double centerZ = (fireballBox.minZ + fireballBox.maxZ) / 2.0;
        return RotationUtils.getRotationsToPoint(centerX, topY, centerZ, baseYaw, basePitch);
    }

    private boolean hitsFireballBox(Vec3d eye, float yaw, float pitch, Box box, double range) {
        Vec3d look = Utils.getLookVec(yaw, pitch);
        Vec3d end = eye.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        HitResult intercept = box.calculateIntercept(eye, end);
        return intercept != null;
    }

    
    public void onPrePlayerInteract(PrePlayerInteractEvent e) {
        if (!Utils.nullCheck() || mc.currentScreen != null) return;
        if (onGround.isToggled() && !mc.player.onGround) return;
        if (fireball == null) return;

        HitResult mop = mc.objectMouseOver;
        if (mop == null || mop.typeOfHit != HitResult.MovingObjectType.ENTITY || mop.entityHit != fireball) {
            nextClickTime = 0;
            ReflectionUtils.setButton(0, false);
            return;
        }

        long now = System.currentTimeMillis();
        if (nextClickTime == 0) {
            nextClickTime = now;
        }

        int key = mc.options.keyBindAttack.getKeyCode();
        while (nextClickTime <= now) {
            KeyBinding.setKeyBindState(key, true);
            KeyBinding.onTick(key);
            ReflectionUtils.setButton(0, true);
            nextClickTime += nextDelay();
        }
    }

    private ExplosiveProjectileEntity getFireball() {
        double rangeSq = range.getInput() * range.getInput();
        float fovVal = (float) fov.getInput();

        if (onGround.isToggled() && !mc.player.onGround) return null;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ExplosiveProjectileEntity)) continue;
            if (!fireballs.contains(entity)) continue;
            if (mc.player.getDistanceSqToEntity(entity) > rangeSq) continue;
            if (fovVal != 360.0f && !Utils.inFov(fovVal, entity)) continue;
            return (ExplosiveProjectileEntity) entity;
        }
        return null;
    }

    
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (!Utils.nullCheck()) return;
        if (e.entity == mc.player) {
            fireballs.clear();
        } else if (e.entity instanceof ExplosiveProjectileEntity && mc.player.getDistanceSqToEntity(e.entity) > 16.0) {
            fireballs.add(e.entity);
        }
    }

    private long nextDelay() {
        int target = Math.max(1, (int) targetCPS.getInput());
        int baseDelay = 1000 / target;
        int variation = rand.nextInt(Math.max(1, baseDelay / 3 + 1)) - baseDelay / 6;
        return Math.max(33, baseDelay + variation);
    }
}
