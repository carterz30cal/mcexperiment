package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class AbilityFishBrain extends ItemAbility {

	public AbilityFishBrain(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "REDFish Brain";
	}

	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYSets your LIGHT_PURPLEMana " + Stat.MANA.getIcon() + "GRAY to LIGHT_PURPLE0GRAY.");
		return l;
	}
	
	@Override
	public void onPlayerStats(StatContainer item) {
		item.scheduleOperation(Stat.MANA, StatOperationType.CAP_MAX, 0);
	}
}
