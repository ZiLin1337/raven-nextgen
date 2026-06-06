package keystrokesmod.utility;

// import IMixinItemRenderer removed
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
// EnumAction removed
import net.minecraft.item.ItemStack;

public final class BlockAnimationUtils {
    private static PlayerEntity renderingPlayer;
    private static int renderingDepth;

    private BlockAnimationUtils() {
    }

    public static void beginRender(PlayerEntity player) {
        if (player == MinecraftClient.getInstance().thePlayer) {
            if (renderingDepth++ == 0) {
                renderingPlayer = player;
            }
        }
    }

    public static void endRender(PlayerEntity player) {
        if (player != renderingPlayer || renderingDepth <= 0) {
            return;
        }

        if (--renderingDepth == 0) {
            renderingPlayer = null;
        }
    }

    public static boolean shouldForceBlockAnimation(PlayerEntity player, ItemStack stack) {
        if (player == null || player != renderingPlayer || stack == null || stack.getItemUseAction() != EnumAction.BLOCK) {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getItemRenderer() != null && ((IMixinItemRenderer) mc.getItemRenderer()).isRenderItemInUse();
    }
}
