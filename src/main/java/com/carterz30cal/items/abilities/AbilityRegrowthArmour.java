package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class AbilityRegrowthArmour extends ItemAbility
{
	public AbilityRegrowthArmour(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GREENRegrowth";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAll GREENDefenceGRAY on this piece is converted");
		l.add("GRAYto REDHealthGRAY at a rate of GREEN1" +Stat.DEFENCE.getIcon() + "GRAY to RED4" + Stat.HEALTH.getIcon() + "GRAY.");
		return l;
	}
	
	public void onItemStatsLate(StatContainer item)
	{
		item.scheduleOperation(Stat.DEFENCE, StatOperationType.CAP_MAX, 0);
		item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, item.getStat(Stat.DEFENCE) * 4);
	}

}
