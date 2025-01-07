package com.carterz30cal.entities.damage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class StatusEffects implements Cloneable {
	public Map<StatusEffect, Integer> effects = new HashMap<>();
	
	@Override
	public StatusEffects clone() {
		StatusEffects copy = new StatusEffects();
		for (StatusEffect effect : effects.keySet())
		{
			copy.effects.put(effect, effects.getOrDefault(effect, 0));
		}
		
		return copy;
	}
	
	public StatusEffects() {

	}
	
	public StatusEffects(ConfigurationSection sect) {
		for (String k : sect.getKeys(false)) {
			StatusEffect e = StatusEffect.valueOf(k);
			effects.put(e, sect.getInt(k));
		}
	}
	
	public static StatusEffects createWithDefaultResistances() {
		StatusEffects statuses = new StatusEffects();
		for (StatusEffect effect : StatusEffect.values())
		{
			statuses.effects.put(effect, effect.defaultResistance);
		}
		return statuses;
	}
	
	public static StatusEffects createWithZero() {
		StatusEffects statuses = new StatusEffects();
		for (StatusEffect effect : StatusEffect.values())
		{
			statuses.effects.put(effect,0);
		}
		return statuses;
	}
	
	public void add(StatusEffects statuses) {
		for (StatusEffect effect : effects.keySet())
		{
			int val = effects.get(effect);
			int val2 = statuses.effects.getOrDefault(effect, 0);
			
			effects.put(effect, val + val2);
		}
	}
	
	public int getStatus(StatusEffect effect) {
		return effects.getOrDefault(effect, 0);
	}
	
	public boolean getImmune(StatusEffect effect) {
		return effects.getOrDefault(effect, 0) == -1;
	}
	
	public boolean isEmpty() {
		for (StatusEffect effect : effects.keySet())
		{
			if (effects.get(effect) == 0) continue;
			return false;
		}
		return true;
	}
}
