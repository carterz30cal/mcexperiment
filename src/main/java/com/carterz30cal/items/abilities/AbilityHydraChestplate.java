package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class AbilityHydraChestplate extends ItemAbility
{
	public AbilityHydraChestplate(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GOLDHydra Hunt";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYWhilst you are fighting a Hydra, gain");
		l.add(display(Stat.HEALTH, 4) + "GRAY and "+ display(Stat.DEFENCE, 1) + "GRAY per AQUAWave.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		if (!BossWaterwayHydra.hasParticipated(owner)) return;
		
		int wave = BossWaterwayHydra.active ? BossWaterwayHydra.wave : 0;
		item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, wave * 4);
		item.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, wave);
	}

}
