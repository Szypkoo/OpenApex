package dev.toru.clients.eventBus;
import lol.apex.Apex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class EventBus {
    private final Map<Class<?>, List<Listener>> listeners = new HashMap<>();

    public void subscribe(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHook.class))
                continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) continue;

            Class<?> eventType = params[0];
            method.setAccessible(true);

            EventHook hook = method.getAnnotation(EventHook.class);
            Listener listener = new Listener(obj, method, hook.priority());
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(listener);

            listeners.get(eventType)
                    .sort(Comparator.comparingInt(l -> l.priority().ordinal()));
        }
    }

    public void unsubscribe(Object obj) {
        for(List<Listener> group : listeners.values()) {
            group.removeIf(listener -> listener.target == obj);
        }
    }

    public void post(Object event) {
        final var list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) return;

        final var listenersCopy = new ArrayList<>(list);

        for (final var l : listenersCopy) {
            try {
                l.method.invoke(l.target, event);
            } catch (InvocationTargetException e) {
                Apex.LOGGER.error("Event handler threw an exception:", e.getCause());
            } catch (IllegalAccessException e) {
                Apex.LOGGER.error("IllegalAccessException from invoking method: {}", l.method);
            } catch (Exception e) {
                Apex.LOGGER.error("Failed to invoke event handler:", e);
            }
        }
    }

    private record Listener(Object target, Method method, EventPriority priority) {}
}