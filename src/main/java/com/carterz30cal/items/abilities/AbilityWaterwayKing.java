package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class AbilityWaterwayKing extends ItemAbility
{

	public AbilityWaterwayKing(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GOLDWaterway Lord";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYWhilst raining, increase the damage");
		l.add("GRAYand might of this item by RED1.5xGRAY.");
		return l;
	}
	
	public void onItemStats(StatContainer item)
	{
		boolean raining = AreaWaterway.isRaining;
		
		if (raining) 
		{
			item.scheduleOperation(Stat.DAMAGE, StatOperationType.MULTIPLY, 1.5);
			item.scheduleOperation(Stat.MIGHT, StatOperationType.MULTIPLY, 1.5);
		}
	}
}
