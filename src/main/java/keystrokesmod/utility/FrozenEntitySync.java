package keystrokesmod.utility;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FrozenEntitySync {
    private static final FrozenEntitySync INSTANCE = new FrozenEntitySync();
    private final Queue<Object> liveEntityQueue = new ConcurrentLinkedQueue<>();

    public static FrozenEntitySync get() {
        return INSTANCE;
    }

    public void queue(Object packet) {
    }

    public void drain() {
    }

    public void clear() {
        liveEntityQueue.clear();
    }
}