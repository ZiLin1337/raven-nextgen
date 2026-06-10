package keystrokesmod.mixin.impl.render;

import org.spongepowered.asm.mixin.Mixin;

// 1.21.4中TileEntityEnderChestRenderer不存在
// 改为混入BlockEntityRenderDispatcher
@Mixin(net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher.class)
public class MixinTileEntityEnderChestRenderer {
}
