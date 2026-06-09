package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import net.minecraft.entity.LivingEntity;

public class KillAura extends Module {
    public static LivingEntity target;
    public static LivingEntity attackingEntity;

    public KillAura() {
        super("KillAura", category.combat);
    }
}
