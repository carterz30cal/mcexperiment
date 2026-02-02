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

/**
 * Obtained in Waterway using the Waterway Cod fishing discoveries
 * @author carterz30cal
 * @since v1.0.0
 */
public class SuperLure extends ItemEnchant {

	public SuperLure(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 3;
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
		reqs.add(new ItemReq("combination_catalyst_shard", 1, 0));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		types.add(ItemType.WAND);
		types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Super Lure");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		//l.add("GRAYGain " + display(Stat.DAMAGE, level) + "GRAY and " + display(Stat.STRENGTH, 5 * level) + "GRAY.");
		l.add("GRAYGain " + display(Stat.FISHING_POWER, 10 * level) + "GRAY and");
		l.add("GRAYalso gain " + display(Stat.MANA, 15 * level) + "GRAY!");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{

		item.scheduleOperation(Stat.FISHING_POWER, StatOperationType.ADD, level * 10);
		item.scheduleOperation(Stat.MANA, StatOperationType.ADD, level * 15);
	}

}
