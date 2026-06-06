package keystrokesmod.script.packet.serverbound;

import keystrokesmod.script.model.Vec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;

public class PacketHandler {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Packet<?> convertCPacket(CPacket cPacket) {
        if (cPacket instanceof C0A) {
            return new HandSwingC2SPacket(mc.player != null ? mc.player.getActiveHand() : Hand.MAIN_HAND);
        }
        return cPacket.convert();
    }

    public static CPacket createChatMessage(String message) {
        return new CPacket(new ChatMessageC2SPacket(message));
    }

    public static CPacket createSwingHand() {
        return new CPacket(new HandSwingC2SPacket(mc.player != null ? mc.player.getActiveHand() : Hand.MAIN_HAND));
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

    public static CPacket createPlayerAction(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
        return new CPacket(new PlayerActionC2SPacket(action, pos, direction));
    }

    public static CPacket createPlayerInput(float sideways, float forward, boolean jumping, boolean sneaking) {
        return new CPacket(new PlayerInputC2SPacket(sideways, forward, jumping, sneaking));
    }

    public static CPacket createCloseHandledScreen(int syncId) {
        return new CPacket(new CloseHandledScreenC2SPacket(syncId));
    }

    public static CPacket createInteractBlock(BlockPos pos, Direction direction, float hitX, float hitY, float hitZ) {
        return new CPacket(new PlayerInteractBlockC2SPacket(
            mc.player != null ? mc.player.getActiveHand() : Hand.MAIN_HAND,
            new BlockHitResult(new Vec3d(hitX, hitY, hitZ), direction, pos, false),
            0
        ));
    }

    public static CPacket createPlayerInteractEntity(int entityId, boolean attack) {
        if (mc.world == null) return new CPacket(null);
        Entity target = mc.world.getEntityById(entityId);
        if (target == null) return new CPacket(null);
        return new CPacket(new PlayerInteractEntityC2SPacket(
            target,
            attack ? PlayerInteractEntityC2SPacket.InteractType.ATTACK : PlayerInteractEntityC2SPacket.InteractType.INTERACT,
            mc.player != null ? mc.player.getActiveHand() : Hand.MAIN_HAND,
            false
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

    public static CPacket createCreativeInventoryAction(int slot, ItemStack stack) {
        return new CPacket(new CreativeInventoryActionC2SPacket(slot, stack));
    }

    public static CPacket createPlayerAbilities(boolean flying) {
        return new CPacket(new UpdatePlayerAbilitiesC2SPacket(
            mc.player != null ? mc.player.getAbilities() : new PlayerAbilities()
        ));
    }

    public static CPacket createUpdateSign(BlockPos pos, String l1, String l2, String l3, String l4) {
        return new CPacket(new UpdateSignC2SPacket(pos, l1, l2, l3, l4));
    }

    public static CPacket convertOldC03Format(Vec3 pos, float yaw, float pitch, boolean ground, boolean hasPos, boolean hasRot) {
        if (hasPos && hasRot) return createPlayerMove(pos.x, pos.y, pos.z, yaw, pitch, ground);
        else if (hasPos) return createPlayerMove(pos.x, pos.y, pos.z, ground);
        else if (hasRot) return new CPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, ground));
        else return createPlayerMove(ground);
    }
}
