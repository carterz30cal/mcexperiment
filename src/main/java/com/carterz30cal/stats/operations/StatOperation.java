package com.carterz30cal.stats.operations;

import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface StatOperation {
    void apply(StatContainer stats);
    String display();

    StatOperationType getOperationType();
}
