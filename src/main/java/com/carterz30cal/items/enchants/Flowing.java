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

public class Flowing extends ItemEnchant {

	public Flowing(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 4;
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
		reqs.add(new ItemReq("combination_catalyst_shard", 1, 0));
		reqs.add(new ItemReq("rotten_fish", level * 16, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.HELMET);
		types.add(ItemType.CHESTPLATE);
		types.add(ItemType.LEGGINGS);
		types.add(ItemType.BOOTS);
		
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Flowing");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.HEALTH, level * 5) + "GRAY.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, level * 5);
	}

}
