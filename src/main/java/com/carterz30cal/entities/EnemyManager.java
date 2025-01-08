package com.carterz30cal.entities;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.carterz30cal.utils.FileUtils;

public class EnemyManager 
{
	public static String[] files = {
			"greenforest/mobs/lunatics",
			"waterway/mobs/drippers","waterway/mobs/drenched","waterway/mobs/shockers",
			"waterway/mobs/titans","waterway/mobs/hydras","waterway/mobs/seraph_fight",
			"waterway/mobs/lurkers","waterway/mobs/dungeon",
			"verglaspeak/mobs/icer","verglaspeak/mobs/blizzard",
			"fishing/mobs/bracket0","fishing/mobs/bracket1"
	};
	
	public static EnemyManager instance;
	
	public EnemyManager()
	{
		instance = this;
		
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
			for (String p : c.getKeys(false))
			{
				EnemyTypes type = EnemyTypes.valueOf(c.getString(p + ".type", file));
				type.generate(c.getConfigurationSection(p));
			}
		}
	}
	
	public static GameEnemy spawn(String type, Location l)
	{
		return AbstractEnemyType.types.get(type).generate(l);
	}
	
	public static AbstractEnemyType getType(String type) {
		return AbstractEnemyType.types.get(type);
	}
}
