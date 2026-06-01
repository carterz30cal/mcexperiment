package com.carterz30cal.entities;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public interface LocatableEntity {
    Location getLocation();

    /**
     * Teleport this entity to a new location in the world.
     *
     * @param location where are we going?
     */
    void teleport(@NotNull Location location);

    default double distance(Location location) {
        return getLocation().distance(location);
    }
}
