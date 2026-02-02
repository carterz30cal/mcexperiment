package com.carterz30cal.items.abilities;

import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class AbilityHydraLeggings extends ItemAbility
{
	public AbilityHydraLeggings(GamePlayer owner) {
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
		l.add("GRAYTake WHITE10%GRAY less damage");
		l.add("GRAYfrom Swarmers, Mites and Dominators.");
		l.add("GRAYWhilst you are fighting a Hydra");
		l.add("GRAYgain " + display(Stat.POWER, 1) + "GRAY per AQUAWaveGRAY.");
		return l;
	}
	
	public int onDamaged(GameEnemy damager, int damage) {
		if (damager.hasTag("AFFECTED_BY_HYDRA_LEGGINGS")) return (int)(damage * 0.9);
		else return damage;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		if (!BossWaterwayHydra.hasParticipated(owner)) return;
		
		int wave = BossWaterwayHydra.active ? BossWaterwayHydra.wave : 0;
		item.scheduleOperation(Stat.POWER, StatOperationType.ADD, wave);
	}

}
