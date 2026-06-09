package keystrokesmod.mixin.interfaces;

public interface ISaturationRenderer {
    default void setVisibleHeldToolItemtips(boolean visible) {}
    default boolean getVisibleHeldToolItemtips() { return false; }
    default void setHeldToolItemtipFade(int fade) {}
    default int getHeldToolItemtipFade() { return 0; }
}
