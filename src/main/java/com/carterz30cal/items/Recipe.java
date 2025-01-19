package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.carterz30cal.utils.StringUtils;

public class Recipe 
{
	public String customName;
	public boolean hideInLevelMenu;
	
	public String id;
	public String item;
	public String enchants;
	
	public String category;
	
	public int time;
	public int amount;
	public int coinCost;
	public Map<String, Integer> items = new HashMap<>();
	
	public String discoveryReq;
	public int discoveryReqLevel;
	
	public int levelRequirement;
	
	public Recipe(ConfigurationSection i)
	{
		customName = i.getString("custom-name", null);
		hideInLevelMenu = i.getBoolean("hide", false);
		
		id = i.getCurrentPath();
		item = i.getString("item", i.getCurrentPath().split("-")[0]);
		enchants = i.getString("enchants", null);
		
		coinCost = i.getInt("coins", 0);
		if (i.contains("items"))
		{
			for (String r : i.getConfigurationSection("items").getKeys(false))
			{
				items.put(r, i.getConfigurationSection("items").getInt(r));
			}
		}
		
		time = StringUtils.convertPrettyTime(i.getString("time", "0s"));
		
		levelRequirement = i.getInt("level", 0);
		ItemFactory.levelRecipes.putIfAbsent(levelRequirement, new ArrayList<>());
		ItemFactory.levelRecipes.get(levelRequirement).add(this);
		
		amount = i.getInt("amount", 1);
		
		ItemFactory.recipes.put(id, this);
		ItemFactory.categories.getOrDefault(i.getString("category", "base"), ItemFactory.baseCategory).recipes.add(id);
		
		discoveryReq = i.getString("discovery", null);
		discoveryReqLevel = i.getInt("discovery-tier", 0);
		if (discoveryReq != null) 
		{
			Collection col = DiscoveryManager.get(discoveryReq);
			col.recipes.putIfAbsent(discoveryReqLevel, new ArrayList<>());
			col.recipes.get(discoveryReqLevel).add(id);
		}
	}
}
