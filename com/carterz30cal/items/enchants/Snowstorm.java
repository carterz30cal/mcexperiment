package com.carterz30cal.items.enchants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class Snowstorm extends ItemEnchant {

	public Snowstorm(GamePlayer owner) {
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
		return level * 2;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", level + 1, 0));
		reqs.add(new ItemReq("blizzard_dust", (int)Math.pow(2, level) * 3, level * 100));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		//types.add(ItemType.WAND);
		//types.add(ItemType.ROD);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Snowstorm");
	}
	
	@Override
	public List<String> description()
	{
		double multi = 0.03 * level;
		
		List<String> l = new ArrayList<>();
		l.add("GRAYGain " + display(Stat.POWER, level * 2) + "GRAY.");
		l.add("GRAYDeal BLUE"+ (1 + multi) + "xGRAY damage to BLUEBlizzardsGRAY.");
		return l;
	}
	
	public void onAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			
			if (enemy.hasTag("SLAYER_BLIZZARD"))
			{
				double multi = 0.03 * level;
				info.damage *= 1 + multi;
			}
		}
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.POWER, StatOperationType.ADD, level * 2);
	}

}
