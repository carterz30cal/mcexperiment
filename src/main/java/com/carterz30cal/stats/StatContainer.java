package com.carterz30cal.stats;

import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.damage.StatusEffects;

import java.util.*;
import java.util.Map.Entry;


public class StatContainer implements Cloneable
{
	public Map<Stat, Integer> stats = new HashMap<>();
	public Map<Integer, List<StatOperation>> scheduled = new HashMap<>();
	
	public StatusEffects statuses = StatusEffects.createWithZero();
	
	public void scheduleOperation(Stat stat, StatOperationType type, double value)
	{
		int priority = type.ordinal();
		
		scheduled.putIfAbsent(priority, new ArrayList<>());
		
		StatOperation operation = new StatOperation();
		operation.stat = stat;
		operation.value = value;
		
		scheduled.get(priority).add(operation);
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
	
	/*
	 * 
	 * 
	 * 
	 */
	//@Description("Requires execution of operations")
	public void pushIntoContainer(StatContainer receiver)
	{
		executeOperations();
		for (Entry<Stat, Integer> stat : stats.entrySet())
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
	
	
	public int getStat(Stat stat)
	{
		return stats.getOrDefault(stat, 0);
	}
	
	public String getDisplayed(Stat stat)
	{
        if (Objects.requireNonNull(stat.display) == StatDisplayType.PERCENTAGE) {
            return getStat(stat) + "%";
        }
        return "" + getStat(stat);
    }
	
	public void setStat(Stat stat, double value)
	{
		stats.put(stat, (int)value);
	}
	public void setStat(Stat stat, int value)
	{
		stats.put(stat, value);
	}
	
	@Override
	public StatContainer clone()
	{
		StatContainer container = new StatContainer();
		container.stats = new HashMap<>(stats);
		container.statuses = statuses.clone();
		return container;
	}
	
	public void executeOperations()
	{
		for (int tasks : scheduled.keySet())
		{
			//System.out.print(tasks);
			
			switch (StatOperationType.values()[tasks])
			{
			case ADD:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, getStat(operation.stat) + operation.value);
				break; 
			case SUBTRACT:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, getStat(operation.stat) - operation.value);
				break;
			case MULTIPLY:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, getStat(operation.stat) * operation.value);
				break;
			case DIVIDE:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, getStat(operation.stat) / operation.value);
				break;
			case SET:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, operation.value);
				break;
			case CAP_MIN:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, Math.max(getStat(operation.stat), operation.value));
				break;
			case CAP_MAX:
				for (StatOperation operation : scheduled.get(tasks)) setStat(operation.stat, Math.min(getStat(operation.stat), operation.value));
				break;
			}
		}
		
		scheduled.clear();
	}
}
