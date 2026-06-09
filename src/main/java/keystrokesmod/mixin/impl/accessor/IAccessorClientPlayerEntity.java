package keystrokesmod.mixin.impl.accessor;

public interface IAccessorClientPlayerEntity {
    default double getLastX() { return 0.0D; }
    default double getLastY() { return 0.0D; }
    default double getLastZ() { return 0.0D; }
    default float getLastYaw() { return 0.0F; }
    default float getLastPitch() { return 0.0F; }
}
