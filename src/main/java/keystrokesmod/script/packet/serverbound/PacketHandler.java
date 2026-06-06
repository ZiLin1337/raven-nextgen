package keystrokesmod.script.packet.serverbound;

import keystrokesmod.script.model.Vec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;

/**
 * Converts CPacket wrappers to actual 1.21.4 Fabric network packets.
 * This enables scripts to construct packets using simple field-based wrappers
 * and have them translated to the real Minecraft packet classes.
 */
public class PacketHandler {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Converts a CPacket wrapper to a real net.minecraft.network.packet.Packet.
     * Returns null if conversion is not possible.
     */
    public static Packet<?> convertCPacket(CPacket cPacket) {
        if (cPacket instanceof C0A) {
            return new HandSwingC2SPacket(mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND);
        }
        return cPacket.convert();
    }

    // ===== Factory methods for common packets =====

    public static CPacket createChatMessage(String message) {
        return new CPacket(new ChatMessageC2SPacket(message));
    }

    public static CPacket createSwingHand() {
        return new CPacket(new HandSwingC2SPacket(
            mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND
        ));
    }

    public static CPacket createPlayerMove(boolean onGround) {
        return new CPacket(new PlayerMoveC2SPacket.OnGroundOnly(onGround));
    }

    public static CPacket createPlayerMove(double x, double y, double z, boolean onGround) {
        return new CPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }

    public static CPacket createPlayerMove(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        return new CPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround));
    }

    public static CPacket createPlayerAction(PlayerActionC2SPacket.Action action, net.minecraft.util.math.BlockPos pos, net.minecraft.util.math.Direction direction) {
        return new CPacket(new PlayerActionC2SPacket(action, pos, direction));
    }

    public static CPacket createPlayerInput(float sideways, float forward, boolean jumping, boolean sneaking) {
        return new CPacket(new PlayerInputC2SPacket(sideways, forward, jumping, sneaking));
    }

    public static CPacket createCloseHandledScreen(int syncId) {
        return new CPacket(new CloseHandledScreenC2SPacket(syncId));
    }

    public static CPacket createInteractBlock(net.minecraft.util.math.BlockPos pos, net.minecraft.util.math.Direction direction, float hitX, float hitY, float hitZ) {
        return new CPacket(new PlayerInteractBlockC2SPacket(
            mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND,
            new net.minecraft.util.hit.BlockHitResult(
                new net.minecraft.util.math.Vec3d(hitX, hitY, hitZ), direction, pos, false
            ),
    public static CPacket createInteractBlock(net.minecraft.util.math.BlockPos pos, net.minecraft.util.math.Direction direction, float hitX, float hitY, float hitZ) {
        return new CPacket(new net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket(
            mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND,
            new net.minecraft.util.hit.BlockHitResult(
                new net.minecraft.util.math.Vec3d(hitX, hitY, hitZ), direction, pos, false
            ),
            0
        ));
    }

    public static CPacket createPlayerInteractEntity(int entityId, boolean attack) {
        if (mc.world == null) return new CPacket(null);
        net.minecraft.entity.Entity target = mc.world.getEntityById(entityId);
        if (target == null) return new CPacket(null);
        return new CPacket(new net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket(
            target,
            attack ? net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractType.ATTACK : net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractType.INTERACT,
            mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND,
            false
        ));
    }

        return new CPacket(new PlayerInteractEntityC2SPacket(
            mc.world != null ? mc.world.getEntityById(entityId) : null,
            attack,
            mc.player != null ? mc.player.getActiveHand() : net.minecraft.util.Hand.MAIN_HAND
        ));
    }

    public static CPacket createClientCommand(ClientCommandC2SPacket.Mode mode) {
        return new CPacket(new ClientCommandC2SPacket(mc.player, mode));
    }

    public static CPacket createUpdateSelectedSlot(int slot) {
        return new CPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static CPacket createKeepAlive(long id) {
        return new CPacket(new KeepAliveC2SPacket(id));
    }

    public static CPacket createCreativeInventoryAction(int slot, net.minecraft.item.ItemStack stack) {
        return new CPacket(new CreativeInventoryActionC2SPacket(slot, stack));
    }

    public static CPacket createPlayerAbilities(boolean flying) {
        return new CPacket(new UpdatePlayerAbilitiesC2SPacket(
            mc.player != null ? mc.player.getAbilities() : new net.minecraft.entity.player.PlayerAbilities()
        ));
    }

    public static CPacket createUpdateSign(net.minecraft.util.math.BlockPos pos, String line1, String line2, String line3, String line4) {
        return new CPacket(new UpdateSignC2SPacket(pos, line1, line2, line3, line4));
    }

    /**
     * Creates a C03 packet from the old CPacket format.
     * This supports backward-compatible script calls.
     */
    public static CPacket convertOldC03Format(Vec3 pos, float yaw, float pitch, boolean ground, boolean hasPos, boolean hasRot) {
        if (hasPos && hasRot) {
            return createPlayerMove(pos.x, pos.y, pos.z, yaw, pitch, ground);
        } else if (hasPos) {
            return createPlayerMove(pos.x, pos.y, pos.z, ground);
        } else if (hasRot) {
            return new CPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, ground));
        } else {
            return createPlayerMove(ground);
        }
    }
}
