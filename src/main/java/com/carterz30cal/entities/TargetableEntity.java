package com.carterz30cal.entities;

import org.bukkit.entity.LivingEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface TargetableEntity {
    /**
     *
     * @return the LivingEntity that we want the vanilla targeting system to target for us.
     */
    LivingEntity getTargetableEntity();
}
