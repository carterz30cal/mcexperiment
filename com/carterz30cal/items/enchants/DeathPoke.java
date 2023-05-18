package com.carterz30cal.items.enchants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class DeathPoke extends ItemEnchant {

	public DeathPoke(GamePlayer owner) {
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
		return 5;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) {
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 8 * (level - 1) + 2, 1800 * (level - 1) + 200));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		types.add(ItemType.BOW);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Deathpoke");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("Any existing WHITEDeath \u2620GRAY buildup is multiplied by RED1.3xGRAY.");
		if (level == 2) l.add("Any " + Stat.VITALITY.getReverse() + "GRAY on this weapon is converted into YELLOWDecayGRAY buildup.");
		l.add("GRAYThen, convert all statuses on this weapon to YELLOWDecay \u2620GRAY.");
		l.add("GRAYLose " + display(Stat.HEALTH, level * 25) + "GRAY.");
		return l;
	}
	public void onItemStatsLate(StatContainer item)
	{
		if (level == 2) item.statuses.effects.put(StatusEffect.DECAY, item.statuses.getStatus(StatusEffect.DECAY) + item.getStat(Stat.VITALITY));
		for (StatusEffect effect : item.statuses.effects.keySet()) {
			if (effect == StatusEffect.DECAY) continue;
			item.statuses.effects.put(StatusEffect.DECAY, item.statuses.getStatus(StatusEffect.DECAY) + item.statuses.getStatus(effect));
			item.statuses.effects.put(effect, 0);
		}
		item.scheduleOperation(Stat.VITALITY, StatOperationType.CAP_MAX, 0);
	}
	@Override
	public void onItemStats(StatContainer item)
	{
		item.scheduleOperation(Stat.HEALTH, StatOperationType.SUBTRACT, level * 25);
		
	}

}
