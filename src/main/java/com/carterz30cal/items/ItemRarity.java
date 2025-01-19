package com.carterz30cal.items;

import org.bukkit.ChatColor;

public enum ItemRarity
{
	COMMON("Common", ChatColor.GRAY),
	UNCOMMON("Uncommon", ChatColor.YELLOW),
	RARE("Rare", ChatColor.GREEN),
	VERY_RARE("Very Rare", ChatColor.BLUE),
	EPIC("Epic", ChatColor.DARK_PURPLE),
	INCREDIBLE("Incredible", ChatColor.LIGHT_PURPLE),
	LEGENDARY("Legendary", ChatColor.GOLD),
	MYSTERIOUS("Mysterious", ChatColor.DARK_AQUA),
	UNOBTAINABLE("Unobtainable", ChatColor.DARK_RED),
	TRASH("Trash", ChatColor.GRAY);
	public String name;
	public ChatColor colour;
	
	private ItemRarity(String name, ChatColor colour)
	{
		this.name = name;
		this.colour = colour;
	}
}
