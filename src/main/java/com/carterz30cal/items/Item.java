package com.carterz30cal.items;

import com.carterz30cal.items.abilities2.Abilities;
import com.carterz30cal.stats.StatContainer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Item 
{
	public String name;
	public String id;
	
	public List<String> description;
	public List<Abilities> abilities = new ArrayList<>();
	public List<String> tags = new ArrayList<>();

	
	public Material material;
	public ItemType type;
	public ItemRarity rarity;
	public boolean glow;

	public long value;

	public String set;
    public String skullProfileId;

	public String discovery;
	public long discoveryProgress;
	
	public StatContainer stats;
	
	public int r;
	public int g;
	public int b;
}
