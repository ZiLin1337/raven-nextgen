package keystrokesmod.module.impl.render;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.util.math.Vec3d;
import keystrokesmod.event.RenderWorldLastEvent;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenTextRenderer;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;


import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;


import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DamageTags extends Module {
    private static final String[] DEPTH_MODES = {"Vanilla", "Visible", "Through walls"};
    private static final int HEALTH_WATCHER_ID = 6;
    private static final int ABSORPTION_WATCHER_ID = 17;
    private static final double MAX_RENDER_DISTANCE_SQ = 4096.0D;
    private static final double RISE_DISTANCE = 1.0D;
    private static final double FRUSTUM_AABB_EXPAND = 0.35D;
    private static final double STACK_RADIUS_SQ = 0.36D;
    private static final float HIDDEN_TEXT_ALPHA = 32.0F / 255.0F;
    private static final float FADE_START = 0.72F;
    private static final int MIN_FONT_ALPHA = 4;
    private static final String[] FONT_OPTIONS = FontManager.getHudFontOptions();

    private final SliderSetting duration;
    private final SliderSetting scale;
    private final SliderSetting font;
    private final SliderSetting depthMode;
    private final ButtonSetting textShadow;
    private final ButtonSetting background;
    private final SliderSetting backgroundOpacity;

    private final List<DamageTag> activeTags = new ArrayList<>();
    private final Map<Integer, HealthSnapshot> trackedStates = new HashMap<>();
    private final ConcurrentLinkedQueue<QueuedMetadataUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();
    private World lastWorld;

    private static class QueuedMetadataUpdate {
        private final int entityId;
        private final boolean hasHealth;
        private final float health;
        private final boolean hasAbsorption;
        private final float absorption;

        private QueuedMetadataUpdate(int entityId, boolean hasHealth, float health, boolean hasAbsorption, float absorption) {
            this.entityId = entityId;
            this.hasHealth = hasHealth;
            this.health = health;
            this.hasAbsorption = hasAbsorption;
            this.absorption = absorption;
        }
    }

    private static class HealthSnapshot {
        private final float health;
        private final float absorption;

        private HealthSnapshot(float health, float absorption) {
            this.health = health;
            this.absorption = absorption;
        }

        private float total() {
            return health + absorption;
        }
    }

    private static class DamageTag {
        private final String text;
        private final int color;
        private final double x;
        private final double y;
        private final double z;
        private final long createdAt;
        private final long durationMs;
        private final Box frustumAabb;
        private final int textHalfWidth;

        private DamageTag(String text, int color, double x, double y, double z, long createdAt, long durationMs, int textHalfWidth) {
            this.text = text;
            this.color = color;
            this.x = x;
            this.y = y;
            this.z = z;
            this.createdAt = createdAt;
            this.durationMs = durationMs;
            this.textHalfWidth = textHalfWidth;
            double e = FRUSTUM_AABB_EXPAND;
            this.frustumAabb = new Box(x - e, y - e, z - e, x + e, y + RISE_DISTANCE + e, z + e);
        }
    }

    public DamageTags() {
        super("Damage Tags", category.render, 0);
        this.registerSetting(duration = new SliderSetting("Duration", " ms", 1200, 0, 2000, 100));
        this.registerSetting(scale = new SliderSetting("Scale", 1.0, 0.5, 3.0, 0.1));
        this.registerSetting(font = new SliderSetting("Font", 0, FONT_OPTIONS));
        this.registerSetting(depthMode = new SliderSetting("Depth", 0, DEPTH_MODES));
        this.registerSetting(textShadow = new ButtonSetting("Text Shadow", false));
        this.registerSetting(background = new ButtonSetting("Background", true));
        this.registerSetting(backgroundOpacity = new SliderSetting("Background Opacity", 0.25, 0.0, 1.0, 0.05));
    }

    @Override
    public void guiUpdate() {
        backgroundOpacity.setVisible(background.isToggled(), this);
    }

    @Override
    public void onEnable() {
        resetState();
        lastWorld = mc.world;
    }

    @Override
    public void onDisable() {
        resetState();
    }

    
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!(e.getPacket() instanceof EntityTrackerUpdateS2CPacket) || mc.world == null) {
            return;
        }

        EntityTrackerUpdateS2CPacket packet = (EntityTrackerUpdateS2CPacket) e.getPacket();
        List<DataTracker.WatchableObject> watchedObjects = packet.func_149376_c();
        if (watchedObjects == null || watchedObjects.isEmpty()) {
            return;
        }

        boolean hasHealth = false;
        float health = 0.0F;
        boolean hasAbsorption = false;
        float absorption = 0.0F;

        for (DataTracker.WatchableObject watchedObject : watchedObjects) {
            if (watchedObject == null || watchedObject.getObject() == null) {
                continue;
            }

            int dataValueId = watchedObject.getDataValueId();
            if (dataValueId == HEALTH_WATCHER_ID && watchedObject.getObject() instanceof Float) {
                hasHealth = true;
                health = (Float) watchedObject.getObject();
            }
            else if (dataValueId == ABSORPTION_WATCHER_ID && watchedObject.getObject() instanceof Float) {
                hasAbsorption = true;
                absorption = (Float) watchedObject.getObject();
            }
        }

        if (!hasHealth && !hasAbsorption) {
            return;
        }

        pendingUpdates.offer(new QueuedMetadataUpdate(packet.getEntityId(), hasHealth, health, hasAbsorption, absorption));
    }

    
    public void onRenderWorldLast(RenderWorldLastEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }

        if (mc.world != lastWorld) {
            resetState();
            lastWorld = mc.world;
        }

        long now = System.currentTimeMillis();
        flushPendingUpdates(e.partialTicks, now);
        updateTrackedStates();
        pruneExpiredTags(now);

        if (activeTags.isEmpty()) {
            return;
        }

        RavenTextRenderer fontRenderer = getDamageTagTextRenderer();
        EntityRenderDispatcher renderManager = mc.getEntityRenderDispatcher();
        if (fontRenderer == null || renderManager == null) {
            return;
        }

        double viewerX = renderManager.viewerPosX;
        double viewerY = renderManager.viewerPosY;
        double viewerZ = renderManager.viewerPosZ;

        int depthOrdinal = (int) depthMode.getInput();
        float scaleMul = (float) scale.getInput();
        boolean bgEnabled = background.isToggled();
        float bgOpacitySlider = (float) backgroundOpacity.getInput();

        RenderSystem.pushAttrib();
        try {
            for (DamageTag tag : activeTags) {
                double dx = tag.x - viewerX;
                double dy = tag.y - viewerY;
                double dz = tag.z - viewerZ;
                if (dx * dx + dy * dy + dz * dz > MAX_RENDER_DISTANCE_SQ) {
                    continue;
                }
                if (!RenderUtils.isInViewFrustum(tag.frustumAabb)) {
                    continue;
                }
                renderTag(tag, now, renderManager, fontRenderer, viewerX, viewerY, viewerZ,
                        depthOrdinal, scaleMul, bgEnabled, bgOpacitySlider);
            }
        }
        finally {
            RenderSystem.popAttrib();
        }
    }

    private void flushPendingUpdates(float partialTicks, long nowMillis) {
        if (mc.world == null) {
            pendingUpdates.clear();
            return;
        }

        QueuedMetadataUpdate update;
        while ((update = pendingUpdates.poll()) != null) {
            Entity entity = mc.world.getEntityByID(update.entityId);
            if (!(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity || entity == mc.player) {
                trackedStates.remove(update.entityId);
                continue;
            }

            LivingEntity living = (LivingEntity) entity;
            HealthSnapshot previous = trackedStates.get(update.entityId);

            float newHealth = update.hasHealth ? update.health : getHealth(living);
            float newAbsorption = update.hasAbsorption ? update.absorption : getAbsorption(living);

            HealthSnapshot current = new HealthSnapshot(newHealth, newAbsorption);
            if (previous == null) {
                trackedStates.put(update.entityId, current);
                continue;
            }

            float delta = current.total() - previous.total();
            trackedStates.put(update.entityId, current);

            if (Math.abs(delta) <= 0.01F) {
                continue;
            }

            spawnTag(living, delta, partialTicks, nowMillis);
        }
    }

    private void updateTrackedStates() {
        if (mc.world == null) {
            trackedStates.clear();
            return;
        }

        trackedStates.entrySet().removeIf(entry -> {
            Entity entity = mc.world.getEntityByID(entry.getKey());
            return !(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity || entity == mc.player;
        });

        List<Entity> loaded = mc.world.getEntities();
        for (int i = 0, n = loaded.size(); i < n; i++) {
            Entity entity = loaded.get(i);
            if (!(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity || entity == mc.player) {
                continue;
            }

            LivingEntity living = (LivingEntity) entity;
            trackedStates.put(entity.getEntityId(), new HealthSnapshot(getHealth(living), getAbsorption(living)));
        }
    }

    private void pruneExpiredTags(long now) {
        Iterator<DamageTag> iterator = activeTags.iterator();
        while (iterator.hasNext()) {
            DamageTag tag = iterator.next();
            if (now - tag.createdAt >= tag.durationMs) {
                iterator.remove();
            }
        }
    }

    private void spawnTag(LivingEntity living, float delta, float partialTicks, long nowMillis) {
        double x = interpolate(living.lastTickPosX, living.posX, partialTicks);
        double y = interpolate(living.lastTickPosY, living.posY, partialTicks) + living.height + 0.5D;
        double z = interpolate(living.lastTickPosZ, living.posZ, partialTicks);
        y += getStackOffset(x, y, z, nowMillis);

        long durationMs = Math.max(1L, Math.round(duration.getInput()));
        String text = (delta > 0.0F ? "+" : "-") + fastOneDecimal(Math.abs(delta));
        int color = delta > 0.0F ? 0xFF55FF55 : 0xFFFF5555;
        RavenTextRenderer fr = getDamageTagTextRenderer();
        int halfW = fr != null ? fr.getStringWidth(text) >> 1 : 0;

        activeTags.add(new DamageTag(text, color, x, y, z, nowMillis, durationMs, halfW));
        if (activeTags.size() > 256) {
            activeTags.remove(0);
        }
    }

    private void renderTag(DamageTag tag, long now, EntityRenderDispatcher renderManager, RavenTextRenderer fontRenderer,
                           double viewerX, double viewerY, double viewerZ,
                           int depthOrdinal, float scaleMul,
                           boolean bgEnabled, float bgOpacitySlider) {
        float progress = getProgress(tag, now);
        if (progress >= 1.0F) {
            return;
        }

        float rise = expoOut(progress);
        float alpha = getAlpha(progress);
        if (alpha <= 0.0F) {
            return;
        }

        double x = tag.x - viewerX;
        double y = tag.y + rise * RISE_DISTANCE - viewerY;
        double z = tag.z - viewerZ;
        float renderScale = 0.02666667F * scaleMul;
        int halfWidth = tag.textHalfWidth;

        RenderSystem.pushMatrix();
        RenderSystem.translate((float) x, (float) y, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        RenderSystem.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotate(renderManager.playerViewX * (mc.options.thirdPersonView == 2 ? -1 : 1), 1.0F, 0.0F, 0.0F);
        RenderSystem.scale(-renderScale, -renderScale, renderScale);

        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        switch (depthOrdinal) {
            case 1:
                renderVisibleTag(fontRenderer, tag, halfWidth, alpha, bgEnabled, bgOpacitySlider);
                break;
            case 2:
                renderThroughWallsTag(fontRenderer, tag, halfWidth, alpha, bgEnabled, bgOpacitySlider);
                break;
            default:
                renderVanillaDepthTag(fontRenderer, tag, halfWidth, alpha, bgEnabled, bgOpacitySlider);
                break;
        }

        RenderSystem.enableDepth();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture2D();
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
        RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.popMatrix();
    }

    private void renderVanillaDepthTag(RavenTextRenderer fontRenderer, DamageTag tag, int halfWidth, float alpha,
                                       boolean bgEnabled, float bgOpacitySlider) {
        RenderSystem.depthMask(false);
        RenderSystem.disableDepth();
        drawBackground(halfWidth, alpha, bgEnabled, bgOpacitySlider);
        fontRenderer.drawString(tag.text, -halfWidth, 0, applyFontAlpha(tag.color, HIDDEN_TEXT_ALPHA * alpha), false);
        RenderSystem.enableDepth();
        RenderSystem.depthMask(true);
        fontRenderer.drawString(tag.text, -halfWidth, 0, applyFontAlpha(tag.color, alpha), textShadow.isToggled());
    }

    private void renderVisibleTag(RavenTextRenderer fontRenderer, DamageTag tag, int halfWidth, float alpha,
                                  boolean bgEnabled, float bgOpacitySlider) {
        RenderSystem.depthMask(false);
        RenderSystem.enableDepth();
        drawBackground(halfWidth, alpha, bgEnabled, bgOpacitySlider);
        fontRenderer.drawString(tag.text, -halfWidth, 0, applyFontAlpha(tag.color, alpha), textShadow.isToggled());
    }

    private void renderThroughWallsTag(RavenTextRenderer fontRenderer, DamageTag tag, int halfWidth, float alpha,
                                       boolean bgEnabled, float bgOpacitySlider) {
        RenderSystem.depthMask(false);
        RenderSystem.disableDepth();
        drawBackground(halfWidth, alpha, bgEnabled, bgOpacitySlider);
        fontRenderer.drawString(tag.text, -halfWidth, 0, applyFontAlpha(tag.color, alpha), textShadow.isToggled());
    }

    private void drawBackground(int halfWidth, float alpha, boolean bgEnabled, float bgOpacitySlider) {
        if (!bgEnabled) {
            return;
        }

        float bgAlpha = bgOpacitySlider * alpha;
        if (bgAlpha <= 0.0F) {
            return;
        }

        RenderSystem.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBufferBuilder();
        worldRenderer.begin(7, VertexFormats.POSITION_COLOR);
        worldRenderer.pos(-halfWidth - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, bgAlpha).endVertex();
        worldRenderer.pos(-halfWidth - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, bgAlpha).endVertex();
        worldRenderer.pos(halfWidth + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, bgAlpha).endVertex();
        worldRenderer.pos(halfWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, bgAlpha).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture2D();
    }

    private float getProgress(DamageTag tag, long now) {
        return Math.clamp_float((float) (now - tag.createdAt) / (float) tag.durationMs, 0.0F, 1.0F);
    }

    private float getAlpha(float progress) {
        if (progress <= FADE_START) {
            return 1.0F;
        }

        float fadeProgress = Math.clamp_float((progress - FADE_START) / (1.0F - FADE_START), 0.0F, 1.0F);
        return 1.0F - fadeProgress;
    }

    private double getStackOffset(double x, double y, double z, long nowMillis) {
        int overlaps = 0;
        for (DamageTag tag : activeTags) {
            if (nowMillis - tag.createdAt >= tag.durationMs) {
                continue;
            }

            double dx = tag.x - x;
            double dz = tag.z - z;
            if ((dx * dx + dz * dz) <= STACK_RADIUS_SQ && Math.abs(tag.y - y) <= 1.5D) {
                overlaps++;
            }
        }
        return overlaps * 0.15D;
    }

    private float getHealth(LivingEntity living) {
        return Math.max(0.0F, living.getHealth());
    }

    private float getAbsorption(LivingEntity living) {
        if (living instanceof PlayerEntity) {
            return Math.max(0.0F, ((PlayerEntity) living).getAbsorptionAmount());
        }
        return 0.0F;
    }

    private double interpolate(double start, double end, float partialTicks) {
        return start + (end - start) * partialTicks;
    }

    private int applyFontAlpha(int color, float alpha) {
        int alphaChannel = Math.clamp_int(Math.round(alpha * 255.0F), 0, 255);
        if (alpha > 0.0F && alphaChannel < MIN_FONT_ALPHA) {
            alphaChannel = MIN_FONT_ALPHA;
        }
        return (alphaChannel << 24) | (color & 0x00FFFFFF);
    }

    private String fastOneDecimal(float value) {
        int tenths = Math.round(value * 10.0F);
        int intPart = tenths / 10;
        int fracPart = Math.abs(tenths % 10);
        if (fracPart == 0) {
            return String.valueOf(intPart);
        }
        return intPart + "." + fracPart;
    }

    private float expoOut(float t) {
        if (t <= 0.0F) {
            return 0.0F;
        }
        if (t >= 1.0F) {
            return 1.0F;
        }
        return 1.0F - (float) Math.pow(2.0D, -10.0D * t);
    }

    private String getSelectedFontName() {
        if (font == null) {
            return FONT_OPTIONS[0];
        }
        int index = (int) Math.max(0, Math.min(font.getOptions().length - 1, font.getInput()));
        return font.getOptions()[index];
    }

    private RavenTextRenderer getDamageTagTextRenderer() {
        return FontManager.getNametagRenderer(getSelectedFontName());
    }

    private void resetState() {
        activeTags.clear();
        trackedStates.clear();
        pendingUpdates.clear();
    }
}
