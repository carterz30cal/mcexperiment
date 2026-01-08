package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.carterz30cal.utils.FileUtils;

public class DiscoveryManager {
	private static Map<String, Collection> discoveries = new HashMap<>();
	private static List<Collection> list = new ArrayList<>();
	private static final String[] files = {
            "waterway2/items/collections"
	};
	
	public DiscoveryManager() 
	{
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
            assert c != null;
            for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);

                assert i != null;
                addNew(new Collection(i));
			}
		}
	}
	
	public static void addNew(Collection discovery)
	{
		discoveries.put(discovery.id, discovery);
		list.add(discovery);
	}
	
	public static Collection get(String id)
	{
		return discoveries.getOrDefault(id, null);
	}
	
	public static List<Collection> getAll()
	{
		return list;
	}
}
