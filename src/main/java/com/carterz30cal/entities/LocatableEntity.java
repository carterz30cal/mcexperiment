package com.carterz30cal.entities;

import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface LocatableEntity {
    Location getLocation();

    default double distance(Location location) {
        return getLocation().distance(location);
    }
}
