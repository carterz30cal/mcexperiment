package com.carterz30cal.stats;

import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.stats.operations.LegacyStatOperation;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class StatContainer implements Cloneable
{
    public final List<com.carterz30cal.stats.operations.StatOperation> operations = new ArrayList<>();
    public Map<Stat, Long> stats = new HashMap<>();
    @Deprecated
	public Map<Integer, List<StatOperation>> scheduled = new HashMap<>();
	
	public StatusEffects statuses = StatusEffects.createWithZero();

    @Deprecated
	public void scheduleOperation(Stat stat, StatOperationType type, double value)
	{
        operations.add(new LegacyStatOperation(type, value, stat));
	}

    /**
     * Schedule an operation for <code>execute()</code>
     *
     * @param operation what operation do we want scheduled?
     * @since 1.0.0
     */
    public void operation(com.carterz30cal.stats.operations.StatOperation operation) {
        operations.add(operation);
    }


    public Set<StatusEffect> getStatuses() {
        var lot = new HashSet<StatusEffect>();
        for (var stat : statuses.effects.entrySet()) {
            if (stat.getValue() > 0) {
                lot.add(stat.getKey());
            }
        }
        return lot;
    }

	public void pushIntoContainer(StatContainer receiver) {
        execute();
        for (Entry<Stat, Long> stat : stats.entrySet())
		{
			receiver.scheduleOperation(stat.getKey(), StatOperationType.ADD, stat.getValue());
		}
		receiver.statuses.add(statuses);
	}
	
	public List<Stat> getStats()
	{
		List<Stat> statList = new ArrayList<>(stats.keySet());
		statList.removeIf((a) -> getStat(a) == 0);
		statList.sort((a, b) -> a.ordinal() < b.ordinal() ? -1 : a == b ? 0 : 1);
		return statList;
    }

    /**
     *
     * @param stat what stat do we want to access?
     * @return the value of the stat, or 0, if it doesn't have a value.
     * @since 1.0.0
     */
    public long stat(Stat stat) {
        return stats.getOrDefault(stat, 0L);
    }

    /**
     * @deprecated in favour of stat(), returns long instead.
     */
    @Deprecated
	public int getStat(Stat stat) {
        return Math.toIntExact(stats.getOrDefault(stat, 0L));
	}
	
	public String getDisplayed(Stat stat)
	{
        if (Objects.requireNonNull(stat.display) == StatDisplayType.PERCENTAGE) {
            return getStat(stat) + "%";
        }
        return "" + getStat(stat);
    }
	
	public void setStat(Stat stat, double value) {
        stats.put(stat, (long)value);
    }

    public void setStat(Stat stat, long value)
	{
		stats.put(stat, value);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
	public StatContainer clone()
	{
		StatContainer container = new StatContainer();
		container.stats = new HashMap<>(stats);
		container.statuses = statuses.clone();
		return container;
    }

    public void execute() {
        operations.sort(Comparator.comparing(com.carterz30cal.stats.operations.StatOperation::getOperationType));
        int size = operations.size();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            operations.get(i).apply(this);
        }
        operations.subList(0, size).clear();
        if (!operations.isEmpty()) {
            execute();
        }
	}
}
