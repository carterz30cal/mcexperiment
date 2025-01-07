package com.carterz30cal.entities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class EntityArmour
{
	public Map<DamageType, Integer> armour = new HashMap<>();
	
	public static EntityArmour generate(ConfigurationSection s)
	{
		EntityArmour armour = new EntityArmour();
		for (String t : s.getKeys(false))
		{
			DamageType type = DamageType.valueOf(t);
			
			armour.armour.put(type, s.getInt(t, 0));
		}
		
		return armour;
	}
	
	public double getModifier(DamageType type)
	{
		return 100D / (100 + armour.getOrDefault(type, 0));
	}
}
