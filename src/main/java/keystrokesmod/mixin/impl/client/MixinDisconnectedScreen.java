package keystrokesmod.mixin.impl.client;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen {
    // AntiDisconnect - 断开连接屏幕
}
