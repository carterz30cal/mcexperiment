package com.carterz30cal.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;

public class ForgingItem
{
	public String item;
	public String data;
	public int amount;
	
	public int time;

	public LocalDateTime finished;
	public boolean isDone;
	public boolean haveNotified;
	
	public ForgingItem(Recipe recipe)
	{
		time = recipe.time;
		item = recipe.item;
		isDone = false;
		haveNotified = false;

		finished = LocalDateTime.now().plusNanos(Math.round((recipe.time / 20d) * 1000000000));
		
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
		finished = LocalDateTime.parse(s.getString("finished"));
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
		f.set("finished", finished.toString());
	}
	
	public ItemStack produce()
	{
		ItemStack i = ItemFactory.build(item, amount);
		
		ItemFactory.setItemData(i, data);
		ItemFactory.update(i, null);
		
		return i;
	}
}
