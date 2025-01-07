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

public class Sharking extends ItemEnchant
{

	public Sharking(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getEnchantPower() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) {
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 10, 50));
		reqs.add(new ItemReq("shark_tooth", 10, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() 
	{
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		types.add(ItemType.WAND);
		types.add(ItemType.BOW);
		types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Sharking");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.POWER, 1) + "GRAY, multiplied by your player level.");
		return l;
	}

	@Override
	public void onItemStats(StatContainer item)
	{
		if (this.owner != null) item.scheduleOperation(Stat.POWER, StatOperationType.ADD, owner.getLevel());
	}

	
}
