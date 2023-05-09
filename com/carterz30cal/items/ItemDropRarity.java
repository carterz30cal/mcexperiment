package com.carterz30cal.items;

public enum ItemDropRarity
{
	NORMAL,
	RARE("BLUEBOLDLucky!", 5),
	EPIC("DARK_PURPLEBOLDEpic!", 0.5),
	LEGENDARY("GOLDBOLDWow!", 0.075),;
	public String messagePrefix;
	public double defaultRarity;
	
	private ItemDropRarity()
	{
		messagePrefix = null;
		defaultRarity = 100;
	}
	
	private ItemDropRarity(String prefix)
	{
		messagePrefix = prefix;
		defaultRarity = -1;
	}
	private ItemDropRarity(String prefix, double rarity)
	{
		messagePrefix = prefix;
		defaultRarity = rarity;
	}
}
