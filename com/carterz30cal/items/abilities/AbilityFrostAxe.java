package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class AbilityFrostAxe extends ItemAbility
{

	public AbilityFrostAxe(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "BLUEIce Cold!";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAll damage is dealt as BLUEfrostGRAY damage.");
		l.add("GRAYWhilst raining, boost this item's BLUEPowerGRAY by BLUE20%GRAY.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		info.type = DamageType.FROST;
	}

	
	public void onItemStats(StatContainer item)
	{
		boolean raining = AreaWaterway.isRaining;
		
		if (raining) item.scheduleOperation(Stat.POWER, StatOperationType.MULTIPLY, 1.2);
	}
}
