package keystrokesmod.script.packet;

import keystrokesmod.script.packet.clientbound.SPacket;
import keystrokesmod.script.packet.serverbound.CPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.c2s.play.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Maps 1.21.4 Fabric packet classes to their common names and provides
 * field extractors via reflection-based access.
 * 
 * This replaces the old 1.8.9 approach of having 30+ individual packet subclasses
 * with a registry-based approach that works across versions.
 */
public class PacketMappings {
    private static final Map<Class<?>, String> CLIENTBOUND_NAMES = new HashMap<>();
    private static final Map<Class<?>, String> SERVERBOUND_NAMES = new HashMap<>();
    private static final Map<String, Class<?>> NAME_TO_CLASS = new HashMap<>();

    static {
        // ===== Clientbound (S2C) packets =====
        registerClientbound(S2CChatMessagePacket.class, "S02", "ChatMessageS2C");
        registerClientbound(S2CGameMessagePacket.class, "S2CGameMessage");
        registerClientbound(S2CPlayerListUpdateS2CPacket.class, "S2CPlayerListUpdate");
        registerClientbound(S2CEntityVelocityUpdateS2CPacket.class, "S12", "EntityVelocityS2C");
        registerClientbound(S2CEntityPositionUpdateS2CPacket.class, "S14", "EntityPositionS2C");
        registerClientbound(S2CEntityAttributesS2CPacket.class, "S2CEntityAttributes");
        registerClientbound(S2CEntityEquipmentUpdateS2CPacket.class, "S2CEntityEquipment");
        registerClientbound(S2CEntityStatusEffectS2CPacket.class, "S2CEntityStatusEffect");
        registerClientbound(S2CEntitySetNameS2CPacket.class, "S2CEntitySetName");
        registerClientbound(S2CParticleS2CPacket.class, "S2CParticle");
        registerClientbound(S2CSoundPacket.class, "S2CSound");
        registerClientbound(S2CExplosionS2CPacket.class, "S2CExplosion");
        registerClientbound(S2CBlockUpdateS2CPacket.class, "S2CBlockUpdate");
        registerClientbound(S2CMultiBlockUpdateS2CPacket.class, "S2CMultiBlockUpdate");
        registerClientbound(S2CChunkDataS2CPacket.class, "S2CChunkData");
        registerClientbound(S2CCommandTreeS2CPacket.class, "S2CCommandTree");
        registerClientbound(S2CKeepAliveS2CPacket.class, "S2CKeepAlive");
        registerClientbound(S2CPlayerRespawnS2CPacket.class, "S2CPlayerRespawn");
        registerClientbound(S2CPlayerPositionLookS2CPacket.class, "S2CPlayerPosLook");
        registerClientbound(S2CPlayerAbilitiesS2CPacket.class, "S2CPlayerAbilities");
        registerClientbound(S2CPlayerHealthS2CPacket.class, "S2CPlayerHealth");
        registerClientbound(S2CPlayerSpawnS2CPacket.class, "S2CPlayerSpawn");
        registerClientbound(S2CInventoryS2CPacket.class, "S2CInventory");
        registerClientbound(S2COpenScreenS2CPacket.class, "S2COpenScreen");
        registerClientbound(S2CSetSlotS2CPacket.class, "S2CSetSlot");
        registerClientbound(S2CPlayerDataS2CPacket.class, "S2CPlayerData");
        registerClientbound(S2CTabCompleteS2CPacket.class, "S2CTabComplete");
        registerClientbound(S2CScoreboardObjectiveUpdateS2CPacket.class, "S2CScoreboardObjective");
        registerClientbound(S2CTeamS2CPacket.class, "S2CTeam");

        // ===== Serverbound (C2S) packets =====
        registerServerbound(ChatMessageC2SPacket.class, "C01", "ChatMessageC2S");
        registerServerbound(PlayerMoveC2SPacket.class, "C03", "PlayerMoveC2S");
        registerServerbound(PlayerInteractEntityC2SPacket.class, "C02", "InteractEntityC2S");
        registerServerbound(ClientCommandC2SPacket.class, "C0A", "ClientCommandC2S");
        registerServerbound(ClickSlotC2SPacket.class, "ClickSlotC2S");
        registerServerbound(CloseHandledScreenC2SPacket.class, "C0D", "CloseScreenC2S");
        registerServerbound(CreativeInventoryActionC2SPacket.class, "CreativeInvC2S");
        registerServerbound(PlayerActionC2SPacket.class, "C0B", "PlayerActionC2S");
        registerServerbound(PlayerInputC2SPacket.class, "C0F", "PlayerInputC2S");
        registerServerbound(KeepAliveC2SPacket.class, "KeepAliveC2S");
        registerServerbound(UpdateSelectedSlotC2SPacket.class, "HeldItemChangeC2S");
        registerServerbound(PlayerInteractBlockC2SPacket.class, "C08", "InteractBlockC2S");
        registerServerbound(PlayerInteractItemC2SPacket.class, "C09", "InteractItemC2S");
        registerServerbound(UpdateSignC2SPacket.class, "UpdateSignC2S");
        registerServerbound(QueryBlockNbtC2SPacket.class, "QueryBlockNbtC2S");
        registerServerbound(AdvancementUpdateC2SPacket.class, "AdvUpdateC2S");
        registerServerbound(HandSwingC2SPacket.class, "HandSwingC2S");
        registerServerbound(UpdatePlayerAbilitiesC2SPacket.class, "AbilitiesC2S");
    }

    private static void registerClientbound(Class<?> clazz, String... names) {
        for (String name : names) {
            CLIENTBOUND_NAMES.put(clazz, name);
            NAME_TO_CLASS.put(name, clazz);
        }
    }

    private static void registerServerbound(Class<?> clazz, String... names) {
        for (String name : names) {
            SERVERBOUND_NAMES.put(clazz, name);
            NAME_TO_CLASS.put(name, clazz);
        }
    }

    public static String getPacketName(Packet<?> packet) {
        Class<?> clazz = packet.getClass();
        String name = CLIENTBOUND_NAMES.get(clazz);
        if (name != null) return name;
        name = SERVERBOUND_NAMES.get(clazz);
        return name != null ? name : clazz.getSimpleName();
    }

    public static boolean isClientbound(Packet<?> packet) {
        return CLIENTBOUND_NAMES.containsKey(packet.getClass());
    }

    public static boolean isServerbound(Packet<?> packet) {
        return SERVERBOUND_NAMES.containsKey(packet.getClass());
    }

    public static Class<?> getPacketClass(String name) {
        return NAME_TO_CLASS.get(name);
    }
}
