package keystrokesmod.mixin.impl.accessor;

public interface IAccessorNetworkManager {
    default int getTicksSinceLastSync() { return 0; }
    default void setTicksSinceLastSync(int ticks) {}
}
