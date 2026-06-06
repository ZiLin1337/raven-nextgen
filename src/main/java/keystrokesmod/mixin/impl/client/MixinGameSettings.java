package keystrokesmod.mixin.impl.client;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.SafeWalk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GameOptions.class)
public class MixinGameSettings {
    @Overwrite
    public static boolean isKeyPressed(KeyBinding key) {
        MinecraftClient mc = mc;
        SafeWalk safewalk = ModuleManager.safeWalk;
        if (key == mc.options.sneakKey && safewalk != null && safewalk.isEnabled() && safewalk.sneak.isToggled() && safewalk.isSneaking) {
            return true;
        }
        return key.isPressed();
    }
}