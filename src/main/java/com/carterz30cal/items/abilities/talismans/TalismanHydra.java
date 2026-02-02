package com.carterz30cal.items.abilities.talismans;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class TalismanHydra extends ItemAbility {

	public TalismanHydra(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "BLUEIncredible Regeneration!";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYEvery time you deal damage, regain RED2\u2665GRAY.");
		l.add("GRAYYou are somewhat more visible.");
		return l;
	}
	
	public void onPlayerStats(StatContainer item) {
		item.scheduleOperation(Stat.VISIBILITY, StatOperationType.ADD, 2);
	}
	
	public void onAttack(DamageInfo info)
	{
		owner.gainHealth(2);
	}

}
