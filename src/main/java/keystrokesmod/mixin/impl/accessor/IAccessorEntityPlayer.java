package keystrokesmod.mixin.impl.accessor;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface IAccessorPlayerEntity {
    @Accessor("experiencePoints")
    int getExperiencePoints();
    @Accessor("experiencePoints")
    void setExperiencePoints(int experiencePoints);

    @Accessor("experience")
    float getExperience();
    @Accessor("experience")
    void setExperience(float experience);

    @Accessor("score")
    int getScore();
    @Accessor("score")
    void setScore(int score);

    @Accessor("attackCooldown")
    int getAttackCooldown();

    @Accessor("attackCooldown")
    void setAttackCooldown(int attackCooldown);
}