package com.carterz30cal.items.abilities.implementation;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class TickingManager {
    private static final Map<Object, BukkitRunnable> runnables = new HashMap<>();

    /**
     * Adds a <code>BukkitRunnable</code> to the map, using any <code>Object</code> as a key
     * @param object the key for the map
     * @param runnable the <code>BukkitRunnable</code> we want in the map
     * @since 1.0.0 [1]
     */
    public static void register(Object object, BukkitRunnable runnable) {
        runnables.put(object, runnable);
    }

    /**
     * Removes a <code>BukkitRunnable</code> from the map, using our <code>Object</code> key
     * @param object the key for the <code>BukkitRunnable</code> we're removing
     * @since 1.0.0 [1]
     */
    public static void deregister(Object object) {
        runnables.remove(object);
    }

    /**
     * Attempt to acquire a <code>BukkitRunnable</code> from the map
     * @param object the key for the <code>BukkitRunnable</code>
     * @return a <code>BukkitRunnable</code>, if one exists in the map, otherwise <code>null</code>
     */
    public static @Nullable BukkitRunnable get(Object object) {
        return runnables.get(object);
    }
}
