package keystrokesmod.mixin.impl.render;

import org.spongepowered.asm.mixin.Mixin;

// 1.21.4中TileEntityEnderChestRenderer不存在
// 此mixin在Fabric环境下为兼容桩
@Mixin(net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher.class)
public class MixinTileEntityChestRenderer {
}
