package com.carterz30cal.stats;

import org.bukkit.ChatColor;

public enum Stat
{
	DAMAGE("\u03C8 Damage", ChatColor.RED, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	SAVAGERY("\u00D7 Savagery", ChatColor.RED, StatType.OFFENSIVE, StatDisplayType.PERCENTAGE),
	STRENGTH("\u25B2 Strength", ChatColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	POWER("\u00B1 Power", ChatColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	MIGHT("\u25BC Might", ChatColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	
	HEALTH("\u2665 Health", ChatColor.RED, StatType.DEFENSIVE, StatDisplayType.NORMAL),
	DEFENCE("\u25CB Defence", ChatColor.GREEN, StatType.DEFENSIVE, StatDisplayType.NORMAL),
	VITALITY("\u25C6 Vitality", ChatColor.GREEN, StatType.DEFENSIVE, StatDisplayType.NORMAL),
	
	MANA("\u00D7 Mana", ChatColor.LIGHT_PURPLE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	FOCUS("\u25C6 Focus", ChatColor.AQUA, StatType.OFFENSIVE, StatDisplayType.NORMAL),
	
	FISHING_POWER("\u03C8 Fishing Power", ChatColor.AQUA, StatType.ECONOMY),
	
	MINING_SPEED("\u023E Breaking Speed", ChatColor.YELLOW, StatType.ECONOMY),
	PICKING("\u0194 Picking", ChatColor.YELLOW, StatType.ECONOMY),
	CLEARING("\u0190 Clearing", ChatColor.YELLOW, StatType.ECONOMY),

	BONUS_COINS("\u00D7 Extra Coins", ChatColor.GOLD, StatType.ECONOMY, StatDisplayType.PERCENTAGE),
	
	ENCHANT_POWER("enchantment power", ChatColor.BLACK, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
	LEVEL_REQUIREMENT("level requirement", ChatColor.BLACK, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
	BACKPACK_PAGES("backpack pages", ChatColor.WHITE, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
	SACK_SPACE("\uD83D Sack Space", ChatColor.WHITE, StatType.HIDDEN, StatDisplayType.NORMAL),
	VISIBILITY("\u25CB Visibility", ChatColor.YELLOW, StatType.OFFENSIVE, StatDisplayType.NORMAL)
	;
	public String name;
	public ChatColor colour;
	public StatType type;
	public StatDisplayType display;
	
	private Stat(String name, ChatColor colour, StatType type, StatDisplayType display)
	{
		this.name = name;
		this.colour = colour;
		this.type = type;
		this.display = display;
	}
	private Stat(String name, ChatColor colour, StatType type)
	{
		this.name = name;
		this.colour = colour;
		this.type = type;
		this.display = StatDisplayType.NORMAL;
	}
	
	public String getReverse()
	{
		return colour + name.substring(2) + " " + name.charAt(0);
	}
	
	public String getIcon()
	{
		return "" + name.charAt(0);
	}
}

/*
DAMAGE("Damage", "\u03C8", ChatColor.RED, true),
STRENGTH("Strength", "\u25B2", ChatColor.RED, true),
POWER("Power", "", ChatColor.RED, true),
MIGHT("Might", "", ChatColor.RED, true),

HEALTH("Health", "\u2665", ChatColor.GREEN, true),
DEFENCE("Defence", "\u25CB", ChatColor.GREEN, true),
VITALITY("Vitality", "\u25C6", ChatColor.GREEN, true),

MANA("Mana", "\u20BC", ChatColor.BLUE, true),
FOCUS("Focus", "\u25A0", ChatColor.BLUE, true),;
*/