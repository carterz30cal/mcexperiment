package com.carterz30cal.entities;

import com.carterz30cal.entities.enemies.*;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
public enum EnemyTypes 
{
	SIMPLE(EnemyTypeSimple.class),
    CAPPED(EnemyTypeDamageCapped.class),
	FISH(EnemyTypeFish.class),
	TITAN(EnemyTypeTitanDrench.class),
	HYDRA(EnemyTypeHydra.class),
	HYDRA_HEAD(EnemyTypeHydraHead.class),
	WRAITHLIKE(EnemyTypeWraithlike.class),
	SERAPH(EnemyTypeSeraph.class),
	SERAPH_MAGE_HEALTH(EnemyTypeSeraphMageHealth.class),
	SERAPH_MAGE_MANA(EnemyTypeSeraphMageMana.class),
	WAVE(EnemyTypeWave.class),
	
	SLAYER_BLIZZARD(EnemyTypeSlayerBlizzard.class);
	
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
