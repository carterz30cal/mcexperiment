package com.carterz30cal.items.enchants;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Blade extends ItemEnchant {

	public Blade(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public int getEnchantPower() {
		// TODO Auto-generated method stub
		return level;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		if (level > 1) reqs.add(new ItemReq("combination_catalyst_shard", level - 1, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Blade");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.DAMAGE, 2) + "GRAY and " + display(Stat.STRENGTH, level * 3) + "GRAY.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.DAMAGE, StatOperationType.ADD, 2);
		item.scheduleOperation(Stat.STRENGTH, StatOperationType.ADD, level * 3);
	}

}
