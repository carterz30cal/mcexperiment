package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class AbilityTitanHammer extends ItemAbility {

	public AbilityTitanHammer(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GREENTitanic Might";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGrants GREEN+1" + Stat.DEFENCE.getIcon() + "GRAY per BLUE2" + Stat.MIGHT.getIcon() + " GRAYon this weapon.");
		return l;
	}
	
	public void onItemStatsLate(StatContainer item)
	{
		item.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, (item.getStat(Stat.MIGHT)+1) / 2);
	}

}
