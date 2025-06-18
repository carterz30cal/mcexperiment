package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.stats.StatContainer;

public class Item 
{
	public String name;
	public String id;
	
	public List<String> description;
	public List<String> abilities = new ArrayList<>();
	public List<String> tags = new ArrayList<>();
	
	public Material material;
	public ItemType type;
	public ItemRarity rarity;
	public boolean glow;

	public long value;

	public String set;

	public String discovery;
	public int discoveryProgress;
	
	public StatContainer stats;
	
	public int r;
	public int g;
	public int b;
}
