package keystrokesmod.mixin.impl.client;

import keystrokesmod.Raven;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {
    // 抽象客户端玩家实体
    // 可用于Skin/Cape模块
}
