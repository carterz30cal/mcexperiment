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

public class Scythelike extends ItemEnchant {

	public Scythelike(GamePlayer owner) {
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
		return level * 10;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 1, 0));
		reqs.add(new ItemReq("leaf_ball", 2, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.TOOL);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Scythelike");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.CLEARING, level) + "GRAY, which increases the amount");
		l.add("GRAYof blocks you clear per break.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.CLEARING, StatOperationType.ADD, level);
	}

}
