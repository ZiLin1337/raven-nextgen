package keystrokesmod.mixin.impl.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface IAccessorMinecraft {
    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int cooldown);
    
    @Accessor("itemUseCooldown")
    int getItemUseCooldown();

    @Invoker("doAttack")
    boolean callClickMouse();
}
