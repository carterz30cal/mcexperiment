package com.carterz30cal.stats.operations;

import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GroupStatOperation implements StatOperation {
    protected final StatOperation[] operations;

    public GroupStatOperation(StatOperation... operations) {
        this.operations = operations;
    }

    @Override
    public void apply(StatContainer stats) {
        for (var operation : operations) {
            operation.apply(stats);
        }
    }

    @Override
    public String display() {
        return "<red>!!!";
    }

    @Override
    public StatOperationType getOperationType() {
        return StatOperationType.ADD;
    }
}
