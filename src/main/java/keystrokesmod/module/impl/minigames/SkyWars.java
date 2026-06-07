package keystrokesmod.module.impl.minigames;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.event.UseItemEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3dd;
// Removed Forge event

import java.awt.*;
import java.util.*;
import java.util.List;

public class SkyWars extends Module {
    public ButtonSetting strengthIndicator;
    public ButtonSetting onlyAuraHostileMobs;
    public ButtonSetting renderTimeWarp;

    public Map<PlayerEntity, Long> strengthPlayers = new HashMap<>();
    private Map<String, SpawnEggInfo> entitySpawnQueue = new LinkedHashMap<>(); // type name, spawn info
    private Map<Vec3d, Long> timeWarpPositions = new LinkedHashMap<>(); // position when thrown, time when thrown
    public List<Integer> spawnedMobs = new ArrayList<>(); // entity id

    private final int STRENGTH_COLOR = new Color(255, 0, 0).getRGB();
    private final int TIME_WARP_COLOR = new Color(210, 0, 255, 64).getRGB();

    private String[] KILL_MESSAGES = new String[] {" by ", " to ", " with ", " of ", " from ", " knight ", " for "};

    private boolean thrownPearl;

    /**
     * A global variable used to determine if the current skywars game you are in is a teams mode or not
     */
    public static boolean isSkyWarsTeams = false;

    public SkyWars() {
        super("Sky Wars", category.minigames);
        this.registerSetting(onlyAuraHostileMobs = new ButtonSetting("Only aura hostile mobs", true));
        this.registerSetting(renderTimeWarp = new ButtonSetting("Render time warp", true));
        this.registerSetting(strengthIndicator = new ButtonSetting("Strength indicator", true));
    }

    @Override
    public void onDisable() {
        this.clear();
    }

    
    public void onPreUpdate(PreUpdateEvent e) {
        if (!strengthIndicator.isToggled() || !Utils.nullCheck() || strengthPlayers.isEmpty() || Utils.getSkyWarsStatus() != 2) {
            return;
        }
        int customMode = getCustomMode();
        if (customMode == 2) {
            return;
        }
        isSkyWarsTeams = customMode == 1;
        long duration = isSkyWarsTeams ? 2000 : 5000;
        ArrayList<PlayerEntity> keysList = new ArrayList<>(strengthPlayers.keySet());
        for (PlayerEntity entityPlayer : keysList) {
            long storedTime = strengthPlayers.get(entityPlayer);
            long timePassed = System.currentTimeMillis() - storedTime;
            if (timePassed < duration && !AntiBot.isBot(entityPlayer)) {
                continue;
            }
            strengthPlayers.remove(entityPlayer);
        }
    }

    
    public void onChat(ClientChatReceivedEvent e) {
        if (e.type == 2 || !Utils.nullCheck()) {
            return;
        }
        String stripped = Utils.stripColor(e.message.getUnformattedText());
        if (stripped.isEmpty()) {
            return;
        }
        if (stripped.equals("You will be warped back in 3 seconds!") && thrownPearl) {
            timeWarpPositions.put(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ), System.currentTimeMillis());
            thrownPearl = false;
            return;
        }
        if (strengthIndicator.isToggled() && Utils.getSkyWarsStatus() == 2) {
            if (getCustomMode() == 2) { // lab, then no
                return;
            }
            if (stripped.endsWith(".") && Arrays.stream(KILL_MESSAGES).anyMatch(stripped::contains)) {
                String[] parts = stripped.split(" ");
                for (String part : parts) {
                    if (!part.endsWith(".")) {
                        continue;
                    }
                    String name = part.substring(0, part.length() - 1);
                    for (PlayerEntity entity : mc.world.getPlayers()) {
                        if (!entity.getName().trim().equals(name) || entity == mc.player) {
                            continue;
                        }
                        strengthPlayers.put(entity, System.currentTimeMillis());
                        break;
                    }
                }
            }
        }
    }

    
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!Utils.nullCheck() || Utils.getSkyWarsStatus() != 2) {
            return;
        }
        if (strengthIndicator.isToggled()) {
            for (PlayerEntity entityPlayer : strengthPlayers.keySet()) {
                if (AntiBot.isBot(entityPlayer)) {
                    continue;
                }
                RenderUtils.renderEntity(entityPlayer, 2, 0, 0, STRENGTH_COLOR, false);
            }
        }
        if (renderTimeWarp.isToggled()) {
            Iterator<Map.Entry<Vec3d, Long>> iterator = this.timeWarpPositions.entrySet().iterator();
            long currentTime = System.currentTimeMillis();

            while (iterator.hasNext()) {
                Map.Entry<Vec3d, Long> entry = iterator.next();
                Vec3d position = entry.getKey();
                long timeThrown = entry.getValue();

                if (currentTime - timeThrown >= 3050) {
                    iterator.remove();
                }
                else {
                    RenderUtils.drawPlayerBoundingBox(position, TIME_WARP_COLOR);
                }
            }
        }
    }

    
    public void onWorldJoin(EntityJoinWorldEvent e) {
        if (e.entity == mc.player) {
            clear();
        }
        else {
            if (e.entity != null) {
                if (Utils.getSkyWarsStatus() != 2) {
                    return;
                }
                String entityClassName = e.entity.getClass().getSimpleName();
                if (entitySpawnQueue.containsKey(entityClassName)) {
                    Vec3d spawnPosition = new Vec3d(e.entity.posX, e.entity.posY, e.entity.posZ);
                    SpawnEggInfo eggInfo = entitySpawnQueue.get(entityClassName);
                    if (eggInfo.spawnPos.distanceTo(spawnPosition) > 3 || Utils.timeBetween(mc.player.ticksExisted, eggInfo.tickSpawned) > 60) { // 3 seconds or not at spawn point then not own mob
                        return;
                    }
                    if (!entitySpawnQueue.remove(entityClassName, eggInfo)) {
                        return;
                    }
                    spawnedMobs.add(e.entity.getEntityId());
                }
            }
        }
    }

    
    public void onSendPacket(SendPacketEvent e) {
        if (e.getPacket() instanceof PlayerInteractBlockC2SPacket) {
            PlayerInteractBlockC2SPacket p = (PlayerInteractBlockC2SPacket) e.getPacket();
            if (p.getPlacedBlockDirection() != 255 && p.getStack() != null && p.getStack().getItem() != null) {
                if (!(p.getStack().getItem() instanceof ItemMonsterPlacer)) {
                    return;
                }
                Class<? extends Entity> oclass = EntityList.stringToClassMapping.get(ItemMonsterPlacer.getEntityName(p.getStack());
                if (oclass == null) {
                    return;
                }
                entitySpawnQueue.put(oclass.getSimpleName(), new SpawnEggInfo(p.getPosition(), mc.player.ticksExisted));
            }
        }
    }

    
    public void onUseItem(UseItemEvent e) {
        if (e.usedItemStack != null && e.usedItemStack.getItem() instanceof ItemEnderPearl && Utils.getSkyWarsStatus() == 2) {
            ItemStack stack = e.usedItemStack;
            if (Utils.stripString(stack.getDisplayName()).equals("Time Warp Pearl")) {
                thrownPearl = true;
            }
            else {
                if (stack.getDisplayName().startsWith("§b§l")) {
                    List<String> toolTip = stack.getTooltip(mc.player, true);
                    if (toolTip != null && toolTip.size() > 1 && Utils.stripString(toolTip.get(1)).contains("Teleports you back to your")) {
                        thrownPearl = true;
                    }
                }
            }
        }
    }

    private void clear() {
        strengthPlayers.clear();
        spawnedMobs.clear();
        entitySpawnQueue.clear();
        timeWarpPositions.clear();
        thrownPearl = false;
    }

    public static boolean onlyAuraHostiles() {
        return ModuleManager.skyWars != null && ModuleManager.skyWars.isEnabled() && ModuleManager.skyWars.onlyAuraHostileMobs.isToggled() && Utils.getSkyWarsStatus() == 2;
    }

    public int getCustomMode() {
        List<String> sidebar = Utils.getSidebarLines();
        if (sidebar.isEmpty()) {
            return -1;
        }
        for (String line : sidebar) {
            line = Utils.stripColor(line);
            if (line.startsWith("Teams left: ")) {
                return 1;
            }
            else if (line.startsWith("Lab: ") || line.startsWith("Mode: Mini")) {
                return 2;
            }
        }
        return -1;
    }

    public static class SpawnEggInfo {
        public Vec3d spawnPos;
        public int tickSpawned;

        public SpawnEggInfo(BlockPos spawnPos, int tickSpawned) {
            this.spawnPos = new Vec3d(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            this.tickSpawned = tickSpawned;
        }
    }
}