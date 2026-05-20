package com.carterz30cal.entities.enemies.directors.behaviour;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface TargetingBehaviour {
    LivingEntity findTarget(Location location);
}
