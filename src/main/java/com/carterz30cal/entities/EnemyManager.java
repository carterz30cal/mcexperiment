package com.carterz30cal.entities;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.carterz30cal.utils.FileUtils;

public class EnemyManager 
{
	public static String[] files = {
            "waterway2/mobs/lunatics","waterway2/mobs/spiders","waterway2/mobs/titans",
			"waterway2/mobs/fishing/fishing_common",
			"waterway2/mobs/fishing/fishing_uncommon",
			"waterway2/mobs/fishing/fishing_rare",
			"waterway2/mobs/fishing/fishing_very_rare",
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
