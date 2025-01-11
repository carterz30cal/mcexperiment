package com.carterz30cal.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.carterz30cal.utils.FileUtils;

public class QuestManager
{
	public static final String[] files = {"waterway2/quests/tier0"};
	
	public QuestManager() {
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
			for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);
				
				new Quest(i);
			}
		}
	}
}
