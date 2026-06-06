package com.carterz30cal.stats.operations;

import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class AddStatOperation implements StatOperation {
    private final Stat stat;
    private final long amount;

    public AddStatOperation(Stat stat, long amount) {
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void apply(StatContainer stats) {
        stats.setStat(stat, stats.stat(stat) + amount);
    }

    @Override
    public StatOperationType getOperationType() {
        return StatOperationType.ADD;
    }
}
