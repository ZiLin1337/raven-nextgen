package keystrokesmod.mixin.impl.accessor;

public interface IAccessorPlayerEntity {
    default int getExperiencePoints() { return 0; }
    default void setExperiencePoints(int experiencePoints) {}
    default float getExperience() { return 0.0F; }
    default void setExperience(float experience) {}
    default int getScore() { return 0; }
    default void setScore(int score) {}
    default int getAttackCooldown() { return 0; }
    default void setAttackCooldown(int attackCooldown) {}
}
