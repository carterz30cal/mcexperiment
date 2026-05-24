package com.carterz30cal.entities;

import com.carterz30cal.entities.enemies.*;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;

@Deprecated
public enum EnemyTypes
{
	SIMPLE(EnemyTypeSimple.class),
    CAPPED(EnemyTypeDamageCapped.class),
	FISH(EnemyTypeFish.class),
	HYDRA(EnemyTypeHydra.class),
	SERAPH(EnemyTypeSeraph.class),
	
	;
	private Class<? extends AbstractEnemyType> type;
	private EnemyTypes(Class<? extends AbstractEnemyType> type)
	{
		this.type = type;
	}
	
	public AbstractEnemyType generate(ConfigurationSection section)
	{
		try {
			return type.getConstructor(ConfigurationSection.class).newInstance(section);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
