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

/**
 * Ready for the revamp, obtained in Waterway at Level 3
 * @author carterz30cal
 * @since v1.0.0
 */
public class Sharpness extends ItemEnchant {

	public Sharpness(GamePlayer owner) {
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
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 1, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		//types.add(ItemType.WAND);
		types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Sharpness");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		//l.add("GRAYGain " + display(Stat.DAMAGE, level) + "GRAY and " + display(Stat.STRENGTH, 5 * level) + "GRAY.");
		l.add("GRAYGain " + display(Stat.STRENGTH, 20 * level) + "GRAY.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		//item.scheduleOperation(Stat.DAMAGE, StatOperationType.ADD, level);
		item.scheduleOperation(Stat.STRENGTH, StatOperationType.ADD, level * 20);
	}

}
