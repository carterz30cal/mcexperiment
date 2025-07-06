package com.carterz30cal.items;

import org.bukkit.ChatColor;

public enum ItemRarity
{
	COMMON("Common", ChatColor.GRAY, 1, 1),
	UNCOMMON("Uncommon", ChatColor.YELLOW, 5, 50),
	RARE("Rare", ChatColor.GREEN, 10, 100),
	VERY_RARE("Very Rare", ChatColor.BLUE, 20, 500),
	EPIC("Epic", ChatColor.DARK_PURPLE, 40, 1000),
	INCREDIBLE("Incredible", ChatColor.LIGHT_PURPLE, 80, 2500),
	LEGENDARY("Legendary", ChatColor.GOLD, 160, 10000),
	MYSTERIOUS("Mysterious", ChatColor.DARK_AQUA),
	UNOBTAINABLE("Unobtainable", ChatColor.DARK_RED),
	TRASH("Trash", ChatColor.GRAY);
	public String name;
	public ChatColor colour;
	public int lootboxOdds = -1;
	public int lootOdds = -1;
	
	ItemRarity(String name, ChatColor colour)
	{
		this.name = name;
		this.colour = colour;
	}

	ItemRarity(String name, ChatColor colour, int lootboxOdds) {
		this.name = name;
		this.colour = colour;
		this.lootboxOdds = lootboxOdds;
	}
	ItemRarity(String name, ChatColor colour, int lootboxOdds, int lootOdds) {
		this.name = name;
		this.colour = colour;
		this.lootboxOdds = lootboxOdds;
		this.lootOdds = lootOdds;
	}
}
