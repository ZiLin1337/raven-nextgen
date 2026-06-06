package keystrokesmod.mixin.impl.accessor;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
// import com.mojang.authlib.GameProfile; // unused
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
