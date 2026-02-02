package com.carterz30cal.items;

import com.carterz30cal.entities.player.GamePlayer;

import java.util.List;
import java.util.Set;

public abstract class ItemEnchant extends ItemAbility
{
	public int level;
	public ItemEnchant(GamePlayer owner) {
		super(owner);
		
		this.level = 1;
	}
	
	public abstract int getMaximumLevel();
	public abstract int getEnchantPower();
	public abstract List<ItemReq> getCatalystRequirements(int level);
	public abstract Set<ItemType> getAppliableTypes();
	
	/*
	 * enchant power
	 * items can only sustain so many enchants
	 * 
	 */
	
	protected String levelColour()
	{
		final int max = getMaximumLevel();
		return level < max ? "DARK_PURPLE" : (level == max ? "BLUE" : "GOLD");
	}
	
	protected String formatName(String name)
	{
		return levelColour() + name + " " + level;
	}
	
	public abstract String name();

}
