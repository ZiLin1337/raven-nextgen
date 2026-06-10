package keystrokesmod.mixin.impl.render;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

// 1.21.4没有OptiFine CustomSky，改用WorldRenderer渲染钩子
@Mixin(ClientWorld.class)
public class MixinCustomSky {
}
