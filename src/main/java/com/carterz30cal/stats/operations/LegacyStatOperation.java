package com.carterz30cal.stats.operations;

import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

/**
 * Used for converting older code into the new operations framework
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class LegacyStatOperation implements StatOperation {
    private final StatOperationType operationType;
    private final double value;
    private final Stat stat;

    public LegacyStatOperation(StatOperationType operationType, double value, Stat stat) {
        this.operationType = operationType;
        this.value = value;
        this.stat = stat;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(StatContainer stats) {
        switch (operationType) {
            case ADD:
                stats.setStat(stat, stats.stat(stat) + value);
                break;
            case SUBTRACT:
                stats.setStat(stat, stats.stat(stat) - value);
                break;
            case MULTIPLY:
                stats.setStat(stat, stats.stat(stat) * value);
                break;
            case DIVIDE:
                stats.setStat(stat, stats.stat(stat) / value);
                break;
            case SET:
                stats.setStat(stat, value);
                break;
            case CAP_MIN:
                stats.setStat(stat, Math.max(stats.stat(stat), value));
                break;
            case CAP_MAX:
                stats.setStat(stat, Math.min(stats.stat(stat), value));
                break;
            default:
                break;
        }
    }

    @Override
    public String display() {
        return "<red>no display!";
    }

    @Override
    public StatOperationType getOperationType() {
        return operationType;
    }
}
