package com.carterz30cal.stats.operations;

import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GrantStatFromStatOperation implements StatOperation {
    private final Stat from;
    private final Stat to;
    private final long ratioFrom;
    private final long ratioTo;

    public GrantStatFromStatOperation(Stat from, Stat to, long ratioFrom, long ratioTo) {
        this.from = from;
        this.to = to;
        this.ratioFrom = ratioFrom;
        this.ratioTo = ratioTo;
    }

    @Override
    public void apply(StatContainer stats) {
        long r = stats.stat(from) / ratioFrom;
        long a = stats.stat(to);
        stats.setStat(to, a + r * ratioTo);
    }

    @Override
    public String display() {
        return "<red>no display!";
    }

    @Override
    public StatOperationType getOperationType() {
        return StatOperationType.LINKAGES;
    }
}
