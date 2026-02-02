package com.carterz30cal.items.enchants;

import com.carterz30cal.entities.damage.StatusEffect;
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
		
		String multi = "1.3x";
		if (level == 2) multi = "1.4x";
		
		l.add("GRAYAny existing WHITEDeath \u2620GRAY buildup is multiplied by RED" + multi + "GRAY.");
		if (level == 2) l.add("GRAYAny " + Stat.VITALITY.getReverse() + "GRAY on this weapon is converted into YELLOWDecayGRAY buildup.");
		l.add("GRAYThen, convert all statuses on this weapon to YELLOWDecay \u2620GRAY.");
		l.add("GRAYLose " + display(Stat.HEALTH, level * 25) + "GRAY.");
		return l;
	}
	public void onItemStatsLate(StatContainer item)
	{
		if (level == 2) item.statuses.effects.put(StatusEffect.DECAY, item.statuses.getStatus(StatusEffect.DECAY) + item.getStat(Stat.VITALITY));
		for (StatusEffect effect : item.statuses.effects.keySet()) {
			if (effect == StatusEffect.DECAY) continue;
			
			int sta = item.statuses.getStatus(effect);
			if (effect == StatusEffect.DEATH) sta = (int)(sta * (1.2D + 0.1D * level));
			
			item.statuses.effects.put(StatusEffect.DECAY, item.statuses.getStatus(StatusEffect.DECAY) + sta);
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
