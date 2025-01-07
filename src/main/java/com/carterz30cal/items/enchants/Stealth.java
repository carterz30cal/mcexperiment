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

public class Stealth extends ItemEnchant {

	public Stealth(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getEnchantPower() {
		// TODO Auto-generated method stub
		return level * 2;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 2 * level, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.HELMET);
		
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Stealth");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYReduces your YELLOWVisibilityGRAY by " + display(Stat.VISIBILITY, level) + "GRAY.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.VISIBILITY, StatOperationType.ADD, -level);
	}

}
