package keystrokesmod.mixin.impl.accessor;

public interface IAccessorArrowEntity {
    default int getLife() { return 0; }
    default void setLife(int life) {}
    default double getAccelerationX() { return 0.0D; }
    default void setAccelerationX(double accelerationX) {}
    default double getAccelerationY() { return 0.0D; }
    default void setAccelerationY(double accelerationY) {}
    default double getAccelerationZ() { return 0.0D; }
    default void setAccelerationZ(double accelerationZ) {}
    default boolean getInGround() { return false; }
}
