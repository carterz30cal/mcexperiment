package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Collection
{
	public String name;
	public String id;
	public String displayItem;
	public List<String> description;
	
	public List<Integer> tiers = new ArrayList<>();
	public List<Integer> xpRewards = new ArrayList<>();
	public Map<Integer, List<String>> recipes = new HashMap<>();
	
	public Collection(ConfigurationSection s)
	{
		name = s.getString("name");
		id = s.getCurrentPath();
		displayItem = s.getString("display-item");
		
		description = s.getStringList("description");
		
		List<String> mod = new ArrayList<>();
		for (String d : description) mod.add("GRAY" + d);
		description = mod;
		
		tiers = s.getIntegerList("tiers");
		xpRewards = s.getIntegerList("xp-rewards");
	}
}
