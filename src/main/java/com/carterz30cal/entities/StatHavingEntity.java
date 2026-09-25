package com.carterz30cal.entities;

import com.carterz30cal.stats.Stat;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface StatHavingEntity {
    long getStat(Stat stat);
}
