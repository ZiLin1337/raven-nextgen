package keystrokesmod.event;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple event bus for 1.21.4
 */
public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final Map<Class<? extends Event>, List<EventHandler>> handlers = new ConcurrentHashMap<>();
    
    public static EventBus getINSTANCE() { return INSTANCE; }
    
    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null && method.getParameterCount() == 1) {
                Class<?> eventType = method.getParameterTypes()[0];
                if (Event.class.isAssignableFrom(eventType)) {
                    method.setAccessible(true);
                    handlers.computeIfAbsent((Class<? extends Event>) eventType, k -> new ArrayList<>())
                        .add(new EventHandler(listener, method, subscribe.priority()));
                }
            }
        }
    }
    
    public void unregister(Object listener) {
        handlers.values().forEach(list -> list.removeIf(h -> h.listener == listener));
    }
    
    public <T extends Event> T post(T event) {
        List<EventHandler> list = handlers.get(event.getClass());
        if (list != null) {
            for (EventHandler handler : list) {
                try {
                    handler.method.invoke(handler.listener, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }
    
    private record EventHandler(Object listener, Method method, int priority) {}
}