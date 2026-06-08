package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.clickgui.components.impl.CategoryComponent;
import keystrokesmod.clickgui.components.impl.ModuleComponent;
import keystrokesmod.helper.RotationHelper;
import keystrokesmod.mixin.impl.accessor.*;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.*;
import keystrokesmod.script.model.*;
import keystrokesmod.script.packet.clientbound.SPacket;
import keystrokesmod.script.packet.serverbound.CPacket;
import keystrokesmod.script.packet.serverbound.PacketHandler;
import keystrokesmod.utility.*;
import keystrokesmod.utility.shader.BlurUtils;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptDefaults {
    private static ExecutorService cachedExecutor;
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Bridge bridge = new Bridge();
    private static final LinkedHashMap<String, Module> modulesMap = new LinkedHashMap<>();

    public static void reloadModules() {
        modulesMap.clear();
        for (Module module : Raven.getModuleManager().getModules()) {
            modulesMap.put(module.getName(), module);
        }
        for (Module module : Raven.scriptManager.scripts.values()) {
            modulesMap.put(module.getName(), module);
        }
    }

    public static class client {
        public static boolean allowFlying() {
            return mc.player != null && mc.player.getAbilities().allowFlying;
        }

        public static void removeStatusEffectInstance(int id) {
            if (mc.player == null) return;
            mc.player.removeStatusEffect(net.minecraft.entity.effect.StatusEffect.byRawId(id));
        }

        public static int getUID() {
            return 4;
        }

        public static String getUser() {
            return "mic";
        }

        public static void addEnemy(String username) {
            Utils.addEnemy(username);
        }

        public static void addFriend(String username) {
            Utils.addFriend(username);
        }

        public static void async(final Runnable method) {
            if (cachedExecutor == null) {
                cachedExecutor = Executors.newCachedThreadPool();
            }
            cachedExecutor.execute(method);
        }

        public static int getFPS() {
            return MinecraftClient.getInstance().getDebugFps();
        }

        public static void chat(String message) {
            if (mc.player != null) mc.player.networkHandler.sendChatMessage(message);
        }

        public static void print(String string) {
            Utils.sendRawMessage(string);
        }

        public static void print(Message component) {
            if (mc.player != null) mc.player.sendMessage(component.component, false);
        }

        public static void print(Object object) {
            String s = String.valueOf(object);
            Utils.sendRawMessage(s);
        }

        public static boolean isDiagonal() {
            return Utils.isDiagonal(false);
        }

        public static void setTimer(float timer) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = timer;
        }

        public static boolean isCreative() {
            return mc.player != null && mc.player.getAbilities().creativeMode;
        }

        public static void processPacket(SPacket packet) {
            if (packet.packet != null) {
                packet.packet.apply(((IAccessorNetworkManager) mc.getNetworkHandler().getNetworkManager()).getPacketListener());
            }
        }

        public static void multiplyMotion(double factor) {
            if (mc.player != null) {
                mc.player.setVelocity(mc.player.getVelocity().multiply(factor, 1, factor));
            }
        }

        public static void processPacketNoEvent(SPacket packet) {
            PacketUtils.receivePacketNoEvent(packet.packet);
        }

        public static String getTitle() {
            return ((IAccessorGuiIngame) mc.inGameHud).getDisplayedTitle();
        }

        public static String getSubTitle() {
            return ((IAccessorGuiIngame) mc.inGameHud).getDisplayedSubTitle();
        }

        public static String getRecordPlaying() {
            return ((IAccessorGuiIngame) mc.inGameHud).getRecordPlaying();
        }

        public static boolean isFlying() {
            return mc.player != null && mc.player.getAbilities().flying;
        }

        public static void attack(Entity entity) {
            if (entity != null && entity.entity instanceof LivingEntity) {
                Utils.attackEntity((LivingEntity) entity.entity, true, true);
            }
        }

        public static boolean isSinglePlayer() {
            return mc.isIntegratedServerRunning();
        }

        public static boolean isSpectator() {
            return mc.player != null && mc.player.isSpectator();
        }

        public static void setFlying(boolean flying) {
            if (mc.player != null) mc.player.getAbilities().flying = flying;
        }

        public static void setJump(boolean jump) {
            if (mc.player != null) mc.player.input.jump = jump;
        }

        public static void setJumping(boolean jump) {
            if (mc.player != null) mc.player.setJumping(jump);
        }

        public static void setRenderArmPitch(float pitch) {
            if (mc.player != null) {
                mc.player.prevRenderArmPitch = pitch;
                mc.player.renderArmPitch = pitch;
            }
        }

        public static float getEquippedProgress() {
            return ((IAccessorItemRenderer) mc.getItemRenderer()).getEquippedProgress();
        }

        public static void disconnect() {
            boolean isLocal = mc.isIntegratedServerRunning();
            boolean isRealms = mc.isConnectedToRealms();
            if (mc.world != null) mc.world.disconnect();
            mc.disconnect();
            if (isLocal) {
                mc.setScreen(new TitleScreen());
                return;
            }
            if (isRealms) {
                mc.setScreen(new TitleScreen());
                return;
            }
            mc.setScreen(new MultiplayerScreen(new TitleScreen()));
        }

        public static float getRenderArmPitch() {
            return mc.player != null ? mc.player.renderArmPitch : 0;
        }

        public static void setRenderArmYaw(float yaw) {
            if (mc.player != null) {
                mc.player.prevRenderArmYaw = yaw;
                mc.player.renderArmYaw = yaw;
            }
        }

        public static float getRenderArmYaw() {
            return mc.player != null ? mc.player.renderArmYaw : 0;
        }

        public static long getTotalMemory() {
            return Runtime.getRuntime().totalMemory();
        }

        public static long getFreeMemory() {
            return Runtime.getRuntime().freeMemory();
        }

        public static long getMaxMemory() {
            return Runtime.getRuntime().maxMemory();
        }

        public static void jump() {
            if (mc.player != null) mc.player.jump();
        }

        public static boolean allowEditing() {
            return mc.player != null && mc.player.getAbilities().allowModifyWorld;
        }

        public static int getItemInUseDuration() {
            return mc.player != null ? mc.player.getItemUseTime() : 0;
        }

        public static void log(final Object obj) {
            Utils.log.info(obj);
        }

        public static void setSneaking(boolean sneak) {
            if (mc.player != null) mc.player.setSneaking(sneak);
        }

        public static void setSneak(boolean sneak) {
            if (mc.player != null) mc.player.input.sneaking = sneak;
        }

        public static boolean isSneak() {
            return mc.player != null && mc.player.input.sneaking;
        }

        public static Entity getPlayer() {
            if (mc.player == null) return null;
            return Entity.convert((net.minecraft.entity.Entity) mc.player);
        }

        public static void removeEnemy(String username) {
            Utils.removeEnemy(username);
        }

        public static void removeFriend(String username) {
            Utils.removeFriend(username);
        }

        public static boolean isRiding() {
            return mc.player != null && mc.player.hasVehicle();
        }

        public static Vec3d getMotion() {
            if (mc.player == null) return new Vec3d(0, 0, 0);
            return new Vec3d(mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z);
        }

        public static void sleep(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
        }

        public static void ping() {
            if (mc.player != null) {
                mc.player.playSound(net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.0f);
            }
        }

        public static void playSound(String name, float volume, float pitch) {
            if (mc.player != null) {
                mc.player.playSound(net.minecraft.registry.Registries.SOUND_EVENT.get(net.minecraft.util.Identifier.of(name)), volume, pitch);
            }
        }

        public static boolean isMoving() {
            return Utils.isMoving();
        }

        public static boolean isJump() {
            return mc.player != null && mc.player.input.jump;
        }

        public static float getStrafe() {
            return mc.player != null ? mc.player.input.movementSideways : 0;
        }

        public static void sleep(int ms) {
            try { Thread.sleep(ms); } catch (Exception e) { e.printStackTrace(); }
        }

        public static float getForward() {
            return mc.player != null ? mc.player.input.movementForward : 0;
        }

        public static void closeScreen() {
            if (mc.currentScreen instanceof ClickGui) {
                mc.setScreen(null);
                return;
            }
            if (mc.player != null) mc.player.closeHandledScreen();
        }

        public static String getScreen() {
            return mc.currentScreen == null ? "" : mc.currentScreen.getClass().getSimpleName();
        }

        public static float[] getRotationsToEntity(Entity entity) {
            if (entity == null || entity.entity == null) return new float[]{0, 0};
            return RotationUtils.getRotations(entity.entity);
        }

        public static void sendPacket(CPacket packet) {
            Packet packet1 = PacketHandler.convertCPacket(packet);
            if (packet1 == null) return;
            if (mc.getNetworkHandler() != null) mc.getNetworkHandler().sendPacket(packet1);
        }

        public static void sendPacketNoEvent(CPacket packet) {
            Packet packet1 = PacketHandler.convertCPacket(packet);
            if (packet1 == null) return;
            PacketUtils.sendPacketNoEvent(packet1);
        }

        public static boolean inFocus() {
            return mc.isWindowFocused();
        }

        public static void dropItem(boolean dropStack) {
            if (mc.player != null) mc.player.dropSelectedItem(dropStack);
        }

        public static void setMotion(double x, double y, double z) {
            if (mc.player != null) mc.player.setVelocity(x, y, z);
        }

        public static void setSpeed(double speed) {
            Utils.setSpeed(speed);
        }

        public static void setForward(float forward) {
            if (mc.player != null) mc.player.input.movementForward = forward;
        }

        public static void setStrafe(float strafe) {
            if (mc.player != null) mc.player.input.movementSideways = strafe;
        }

        public static String getServerIP() {
            if (mc.getCurrentServerEntry() == null || mc.isIntegratedServerRunning()) return "";
            return mc.getCurrentServerEntry().address;
        }

        public static int[] getDisplaySize() {
            Window window = mc.getWindow();
            return new int[]{window.getScaledWidth(), window.getScaledHeight(), (int)window.getScaleFactor()};
        }

        public static float getServerDirection(PlayerState state) {
            return state.yaw;
        }

        // --- Raycast helpers ---

        private static Vec3d getLookVec(float yaw, float pitch) {
            float radYaw = (float) Math.toRadians(yaw);
            float radPitch = (float) Math.toRadians(pitch);
            float cosPitch = (float) Math.cos(radPitch);
            return new Vec3d(-Math.sin(radYaw) * cosPitch, -Math.sin(radPitch), Math.cos(radYaw) * cosPitch);
        }

        public static Object[] raycastBlock(final double distance) {
            if (mc.player == null) return null;
            return raycastBlock(distance, mc.player.getYaw(), mc.player.getPitch());
        }

        public static Object[] raycastBlock(final double distance, final float yaw, final float pitch) {
            if (mc.world == null || mc.player == null) return null;
            Vec3d eyeVec = mc.player.getCameraPosVec(1.0f);
            Vec3d lookVec = getLookVec(yaw, pitch);
            Vec3d sumVec = eyeVec.add(lookVec.multiply(distance));
            BlockHitResult hit = mc.world.raycast(new RaycastContext(eyeVec, sumVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            if (hit == null || hit.getType() != HitResult.Type.BLOCK) return null;
            BlockPos pos = hit.getBlockPos();
            Vec3d posW = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            Vec3d offset = new Vec3d(hit.getPos().x - pos.getX(), hit.getPos().y - pos.getY(), hit.getPos().z - pos.getZ());
            return new Object[] { posW, offset, hit.getSide().name() };
        }

        public static Object[] raycastEntity(final double distance) {
            if (mc.player == null) return null;
            return raycastEntity(distance, mc.player.getYaw(), mc.player.getPitch());
        }

        public static Object[] raycastEntity(final double distance, final float yaw, final float pitch) {
            if (mc.world == null || mc.player == null) return null;
            net.minecraft.entity.Entity pointedEntity = null;
            Vec3d eyeVec = mc.player.getCameraPosVec(1.0f);
            Vec3d lookVec = getLookVec(yaw, pitch);
            Vec3d reachVec = eyeVec.add(lookVec.multiply(distance));
            EntityHitResult mop = ProjectileUtil.raycast(mc.player, eyeVec, reachVec, mc.player.getBoundingBox().stretch(lookVec.multiply(distance)).expand(1.0), (entity) -> !entity.isSpectator() && entity.canHit(), distance);
            if (mop != null && mop.getEntity() != null) {
                Vec3d offset = new Vec3d(mop.getPos().x - mop.getEntity().getX(), mop.getPos().y - mop.getEntity().getY(), mop.getPos().z - mop.getEntity().getZ());
                return new Object[] { new Entity(mop.getEntity()), offset, eyeVec.squaredDistanceTo(mop.getPos()) };
            }
            // fallback: manual entity search
            net.minecraft.entity.Entity best = null;
            double bestDist = distance;
            Vec3d bestHit = null;
            for (net.minecraft.entity.Entity e : mc.world.getEntities()) {
                if (e.isSpectator() || e == mc.player || !e.canHit()) continue;
                Vec3d rel = e.getBoundingBox().raycast(eyeVec, reachVec).orElse(null);
                if (rel != null) {
                    double d = eyeVec.distanceTo(rel);
                    if (d < bestDist) {
                        best = e;
                        bestDist = d;
                        bestHit = rel;
                    }
                }
            }
            if (best != null) {
                return new Object[] { new Entity(best), new Vec3d(bestHit.x - best.getX(), bestHit.y - best.getY(), bestHit.z - best.getZ()), eyeVec.squaredDistanceTo(bestHit) };
            }
            return null;
        }

        public static boolean canPlaceBlock(ItemStack stack, Vec3d pos, String side) {
            if (stack == null || stack.itemStack == null || stack.itemStack.isEmpty() || !(stack.itemStack.getItem() instanceof BlockItem)) return false;
            if (mc.world == null || mc.player == null) return false;
            BlockPos targetPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            Direction dir = Direction.byName(side);
            if (dir == null) dir = Direction.UP;
            return ((BlockItem) stack.itemStack.getItem()).canPlaceAt(mc.world, targetPos.offset(dir));
        }

        public static boolean placeBlock(Vec3d targetPos, String side, Vec3d hitVec) {
            if (mc.interactionManager == null || mc.player == null || mc.world == null) return false;
            Direction dir = Direction.byName(side);
            if (dir == null) dir = Direction.UP;
            BlockPos pos = new BlockPos((int) targetPos.x, (int) targetPos.y, (int) targetPos.z);
            Vec3d hit = new Vec3d(hitVec.x, hitVec.y, hitVec.z);
            return mc.interactionManager.interactBlock(mc.player, mc.player.getActiveHand(), pos, dir, hit) == net.minecraft.util.ActionResult.SUCCESS;
        }

        public static void enableMovementFix() {
            RotationHelper.get().forceMovementFix = true;
        }

        public static void disableMovementFix() {
            RotationHelper.get().forceMovementFix = false;
        }

        public static boolean isMovementFixActive() {
            return RotationHelper.get().fixMovement();
        }

        public static boolean isRotationActive() {
            return RotationHelper.get().isActive();
        }

        public static void setRotations(float yaw, float pitch) {
            RotationHelper.get().setRotations(yaw, pitch);
        }

        public static void setYaw(float yaw) {
            RotationHelper.get().setYaw(yaw);
        }

        public static void setPitch(float pitch) {
            RotationHelper.get().setPitch(pitch);
        }

        public static Float getServerYaw() {
            return RotationHelper.get().getServerYaw();
        }

        public static Float getServerPitch() {
            return RotationHelper.get().getServerPitch();
        }

        public static float[] getRotationsToBlock(Vec3d position) {
            BlockPos bp = new BlockPos((int) position.x, (int) position.y, (int) position.z);
            return RotationUtils.getRotations(bp);
        }

        public static void setSprinting(boolean sprinting) {
            if (mc.player != null) mc.player.setSprinting(sprinting);
        }

        public static void swing() {
            if (mc.player != null) mc.player.swingHand(mc.player.getActiveHand());
        }

        public static long time() {
            return System.currentTimeMillis();
        }

        public static boolean isFriend(String username) {
            return Utils.isFriended(username);
        }

        public static boolean isEnemy(String username) {
            return Utils.isEnemy(username);
        }
    }

    public static class world {
        public static Block getBlockAt(int x, int y, int z) {
            BlockState state = BlockUtils.getBlockState(new BlockPos(x, y, z));
            if (state == null) return new Block(null, new BlockPos(x, y, z));
            return new Block(state, new BlockPos(x, y, z));
        }

        public static Block getBlockAt(Vec3d pos) {
            BlockPos bp = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            BlockState state = BlockUtils.getBlockState(bp);
            if (state == null) return new Block(null, bp);
            return new Block(state, bp);
        }

        public static String getDimension() {
            if (mc.world == null) return "";
            return mc.world.getRegistryKey().getValue().toString();
        }

        public static List<Entity> getEntities() {
            List<Entity> entities = new ArrayList<>();
            if (mc.world == null) return entities;
            for (net.minecraft.entity.Entity entity : mc.world.getEntities()) {
                entities.add(Entity.convert(entity));
            }
            return entities;
        }

        public static Entity getEntityById(int entityId) {
            if (mc.world == null) return null;
            return Entity.convert(mc.world.getEntityById(entityId));
        }

        public static List<NetworkPlayer> getNetworkPlayers() {
            List<NetworkPlayer> list = new ArrayList<>();
            for (PlayerListEntry entry : Utils.getTablist(false)) {
                list.add(NetworkPlayer.convert(entry));
            }
            return list;
        }

        public static List<Entity> getPlayerEntities() {
            List<Entity> entities = new ArrayList<>();
            if (mc.world == null) return entities;
            for (net.minecraft.entity.Entity entity : mc.world.getPlayers()) {
                entities.add(Entity.convert(entity));
            }
            return entities;
        }

        public static List<String> getScoreboard() {
            List<String> lines = Utils.getSidebarLines();
            if (lines.isEmpty()) return null;
            return lines;
        }

        public static String getTabHeader() {
            if (mc.inGameHud == null || mc.inGameHud.getPlayerListHud() == null) return "";
            var header = ((IAccessorGuiPlayerTabOverlay) mc.inGameHud.getPlayerListHud()).getHeader();
            return header != null ? header.getString() : "";
        }

        public static String getTabFooter() {
            if (mc.inGameHud == null || mc.inGameHud.getPlayerListHud() == null) return "";
            var footer = ((IAccessorGuiPlayerTabOverlay) mc.inGameHud.getPlayerListHud()).getFooter();
            return footer != null ? footer.getString() : "";
        }

        public static Map<String, List<String>> getTeams() {
            Map<String, List<String>> teams = new HashMap<>();
            if (mc.world == null) return teams;
            for (Team team : mc.world.getScoreboard().getTeams()) {
                List<String> members = new ArrayList<>(team.getPlayerList());
                teams.put(team.getName().getString(), members);
            }
            return teams;
        }

        public static List<BlockEntity> getTileEntities() {
            List<BlockEntity> list = new ArrayList<>();
            if (mc.world == null) return list;
            for (var be : mc.world.blockEntities) {
                list.add(new BlockEntity(be));
            }
            return list;
        }
    }

    public static class modules {
        private final String superName;

        public modules(String superName) {
            this.superName = superName;
        }

        private static Module getModule(String moduleName) {
            return modulesMap.get(moduleName);
        }

        private Module getScript(String name) {
            return modulesMap.get(name);
        }

        private static Setting getSetting(Module module, String settingName) {
            if (module == null) return null;
            for (Setting setting : module.getSettings()) {
                if (setting.getName().equals(settingName)) return setting;
            }
            return null;
        }

        private GroupSetting getGroupForString(String group) {
            if (group.isEmpty()) return null;
            List<Setting> settings = getScript(this.superName).getSettings();
            for (Setting setting : settings) {
                if (setting instanceof GroupSetting && setting.getName().equals(group)) {
                    return (GroupSetting) setting;
                }
            }
            return null;
        }

        public void enable(String moduleName) {
            Module m = getModule(moduleName);
            if (m != null) m.enable();
        }

        public void disable(String moduleName) {
            Module m = getModule(moduleName);
            if (m != null) m.disable();
        }

        public boolean isEnabled(String moduleName) {
            Module m = getModule(moduleName);
            return m != null && m.isEnabled();
        }

        public Entity getKillAuraTarget() {
            if (KillAura.target == null) return null;
            return Entity.convert(KillAura.target);
        }

        public Map<String, Object> getSettings(String name) {
            Map<String, Object> settings = new HashMap<>();
            Module module = getModule(name);
            if (module == null) return settings;
            for (Setting setting : module.getSettings()) {
                if (setting instanceof SliderSetting s) {
                    settings.put(setting.getName(), s.getInput());
                } else if (setting instanceof ButtonSetting b) {
                    settings.put(setting.getName(), b.isToggled());
                } else if (setting instanceof ColorSetting c) {
                    settings.put(setting.getName(), c.getColor());
                }
            }
            return settings;
        }

        public Map<String, List<String>> getCategories() {
            Map<String, List<String>> categories = new HashMap<>();
            for (CategoryComponent categoryComponent : ClickGui.categories) {
                List<String> mods = new ArrayList<>();
                for (ModuleComponent module : categoryComponent.modules) {
                    mods.add(module.mod.getName());
                }
                categories.put(categoryComponent.category.name(), mods);
            }
            return categories;
        }

        public Vec3d getBedAuraPosition() {
            if (ModuleManager.bedAura == null || !ModuleManager.bedAura.isEnabled()) return null;
            BlockPos p = ModuleManager.bedAura.getAuraTargetPos();
            return p != null ? new Vec3d(p.getX(), p.getY(), p.getZ()) : null;
        }

        public float[] getBedAuraProgress() {
            if (ModuleManager.bedAura == null || !ModuleManager.bedAura.isEnabled()) return new float[]{0, 0};
            float d = ModuleManager.bedAura.getAuraBreakProgress();
            return new float[]{d, 0};
        }

        public boolean isScaffolding() { return false; }
        public boolean isTowering() { return false; }

        public boolean isHidden(String moduleName) {
            Module m = getModule(moduleName);
            return m != null && m.isHidden();
        }

        public void registerGroup(String name) {
            getScript(this.superName).registerSetting(new GroupSetting(name));
        }

        public void registerButton(String name, boolean defaultValue) {
            getScript(this.superName).registerSetting(new ButtonSetting(name, defaultValue));
        }

        public void registerButton(String group, String name, boolean defaultValue) {
            getScript(this.superName).registerSetting(new ButtonSetting(getGroupForString(group), name, defaultValue));
        }

        public void registerKey(String group, String name, int defaultKey) {
            getScript(this.superName).registerSetting(new KeySetting(getGroupForString(group), name, defaultKey));
        }

        public void registerKey(String name, int defaultKey) {
            getScript(this.superName).registerSetting(new KeySetting(name, defaultKey));
        }

        public void registerSlider(String group, String name, String suffix, double defaultValue, double minimum, double maximum, double interval) {
            getScript(this.superName).registerSetting(new SliderSetting(getGroupForString(group), name, suffix, defaultValue, minimum, maximum, interval));
        }

        public void registerSlider(String group, String name, String suffix, int defaultValue, String[] stringArray) {
            getScript(this.superName).registerSetting(new SliderSetting(getGroupForString(group), name, suffix, defaultValue, stringArray));
        }

        public void registerSlider(String name, double defaultValue, double minimum, double maximum, double interval) {
            this.registerSlider("", name, "", defaultValue, minimum, maximum, interval);
        }

        public void registerSlider(String name, int defaultValue, String[] stringArray) {
            this.registerSlider("", name, "", defaultValue, stringArray);
        }

        // Setter methods
        public void setSlider(String name, double value) { setSlider("", name, value); }
        public void setSlider(String group, String name, double value) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            if (s instanceof SliderSetting slider) slider.setValue(value);
        }

        public void setSlider(String name, int index) { setSlider("", name, index); }
        public void setSlider(String group, String name, int index) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            if (s instanceof SliderSetting slider) slider.setValue(index);
        }

        public void setButton(String name, boolean value) { setButton("", name, value); }
        public void setButton(String group, String name, boolean value) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            if (s instanceof ButtonSetting button) button.setState(value);
        }

        public boolean getButton(String name) { return getButton("", name); }
        public boolean getButton(String group, String name) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            return s instanceof ButtonSetting && ((ButtonSetting) s).isToggled();
        }

        public double getSlider(String name) { return getSlider("", name); }
        public double getSlider(String group, String name) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            return s instanceof SliderSetting slider ? slider.getInput() : 0;
        }

        public int getSliderIndex(String name) { return getSliderIndex("", name); }
        public int getSliderIndex(String group, String name) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            return s instanceof SliderSetting slider ? slider.getValue() : 0;
        }

        public boolean isSlider(String name) { return isSlider("", name); }
        public boolean isSlider(String group, String name) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            return s instanceof SliderSetting;
        }

        public boolean isButton(String name) { return isButton("", name); }
        public boolean isButton(String group, String name) {
            Setting s = getGroupForString(group) != null
                ? getSetting(getScript(name), name)
                : getModule(name) != null ? getSetting(getModule(name), name) : null;
            return s instanceof ButtonSetting;
        }

        public String getGroup(String setting) { return ""; }
    }

    public static class Bridge extends modules {
        public Bridge() {
            super("Bridge");
        }
    }
}
