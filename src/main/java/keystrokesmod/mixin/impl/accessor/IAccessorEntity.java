package keystrokesmod.mixin.impl.accessor;

public interface IAccessorEntity {
    default int getFire() { return 0; }
    default void setFire(int fire) {}
    default int getNextStepDistance() { return 0; }
    default void setNextStepDistance(int nextStepDistance) {}
    default boolean getIsInWeb() { return false; }
    default void setIsInWeb(boolean isInWeb) {}
    default int getAir() { return 0; }
    default void setAir(int air) {}
    default boolean getLateral() { return false; }
    default void setLateral(boolean lateral) {}
}
