package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class RecipeCategory 
{
	public String id;
	public String name;
	public String icon;
	public List<String> description;
	
	public String parent;
	public List<String> subcategories = new ArrayList<>();
	public List<String> recipes = new ArrayList<>();
	
	public RecipeCategory()
	{
		id = "base";
		name = "All Recipes";
		icon = null;
		parent = null;
		description = new ArrayList<>();
	}
	public RecipeCategory(ConfigurationSection i)
	{
		id = i.getCurrentPath();
		name = "WHITE" + i.getString("name");
		icon = i.getString("icon", "BARRIER");
		description = i.getStringList("description");
		
		parent = i.getString("parent", "base");
		ItemFactory.categories.put(id, this);
	}
	
	public List<Recipe> getRecipes(boolean includeChildren)
	{
		List<Recipe> re = new ArrayList<>();
		for (String r : recipes) re.add(ItemFactory.recipes.get(r));
		
		if (includeChildren)
		{
			for (String c : subcategories) re.addAll(ItemFactory.categories.get(c).getRecipes(true));
		}
		
		return re;
	}
}
