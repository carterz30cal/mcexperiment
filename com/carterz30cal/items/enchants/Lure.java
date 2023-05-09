package com.carterz30cal.items.enchants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class Lure extends ItemEnchant {

	public Lure(GamePlayer owner) {
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
		return level * 5;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", level, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Lure");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.FISHING_POWER, level * 5) + "GRAY.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.FISHING_POWER, StatOperationType.ADD, level * 5);
	}

}
