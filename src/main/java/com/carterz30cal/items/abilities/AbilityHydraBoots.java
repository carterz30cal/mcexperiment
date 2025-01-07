package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class AbilityHydraBoots extends ItemAbility
{
	public AbilityHydraBoots(GamePlayer owner) {
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
		l.add("GRAYTake WHITE25%GRAY more damage from");
		l.add("GRAYObliterators and Deathbringers.");
		l.add("GRAYWhilst fighting a Hydra, you will");
		l.add("GRAYgain " + display(Stat.MANA, 3) + "GRAY and " + display(Stat.FOCUS, 1) + "GRAY per AQUAWaveGRAY.");
		return l;
	}
	
	public int onDamaged(GameEnemy damager, int damage) {
		if (damager.hasTag("AFFECTED_BY_HYDRA_BOOTS")) return (int)(damage * 1.25);
		else return damage;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		if (!BossWaterwayHydra.hasParticipated(owner)) return;
		
		int wave = BossWaterwayHydra.active ? BossWaterwayHydra.wave : 0;
		item.scheduleOperation(Stat.MANA, StatOperationType.ADD, wave * 3);
		item.scheduleOperation(Stat.FOCUS, StatOperationType.ADD, wave);
	}

}
