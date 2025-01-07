package com.carterz30cal.items.enchants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class Shocked extends ItemEnchant
{

	public Shocked(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public int getEnchantPower() {
		// TODO Auto-generated method stub
		return level * 2;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) {
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 1, 0));
		reqs.add(new ItemReq("storm_crystals", level * 4, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() 
	{
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		types.add(ItemType.TOME);
		types.add(ItemType.WAND);
		types.add(ItemType.BOW);
		types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Shocked");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYWhilst it is raining, gain " + display(Stat.POWER, level * 5) + "GRAY.");
		return l;
	}

	@Override
	public void onItemStats(StatContainer item)
	{
		if (AreaWaterway.isRaining) item.scheduleOperation(Stat.POWER, StatOperationType.ADD, level * 5);
	}

	
}
