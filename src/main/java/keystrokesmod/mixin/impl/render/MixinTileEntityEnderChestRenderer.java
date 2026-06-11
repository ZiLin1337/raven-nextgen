package keystrokesmod.mixin.impl.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// 1.21.4中EnderChest渲染集成到通用BlockEntityRenderDispatcher
// 此文件保留为兼容桩，避免mixins.json报错
@Pseudo
@Mixin(targets = "net.minecraft.client.render.block.entity.EnderChestBlockEntityRenderer", remap = false)
public class MixinTileEntityEnderChestRenderer {
}
