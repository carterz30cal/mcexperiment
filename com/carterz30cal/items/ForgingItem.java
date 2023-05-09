package com.carterz30cal.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ForgingItem
{
	public String item;
	public String data;
	public int amount;
	
	public int time;
	
	public ForgingItem(Recipe recipe)
	{
		time = recipe.time;
		item = recipe.item;
		
		amount = recipe.amount;
		if (recipe.enchants != null) data = "enchants:" + recipe.enchants;
		else data = "";
	}
	
	// path should be forging.<id>.
	public ForgingItem(ConfigurationSection s)
	{
		item = s.getString("item");
		data = s.getString("data");
		amount = s.getInt("amount", 1);
		time = s.getInt("time", 0);
	}
	// path should be forging.
	public void save(ConfigurationSection s)
	{
		int id = s.getKeys(false).size();
		s.createSection(id + "");
		ConfigurationSection f = s.getConfigurationSection(id + "");
		
		f.set("item", item);
		f.set("data", data);
		f.set("amount", amount);
		f.set("time", time);
	}
	
	public ItemStack produce()
	{
		ItemStack i = ItemFactory.build(item, amount);
		
		ItemFactory.setItemData(i, data);
		ItemFactory.update(i, null);
		
		return i;
	}
}
