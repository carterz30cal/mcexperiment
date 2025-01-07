package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class DungeonAbilitySword extends ItemAbility {

	public DungeonAbilitySword(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GREENDungeon Sword";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("Whilst in a dungeon, this item has BLUE5xGRAY the stats.");
		return l;
	}
	
	public void onItemStatsLate(StatContainer item)
	{
		if (owner.inDungeon()) for (Stat s : Stat.values()) item.scheduleOperation(s, StatOperationType.MULTIPLY, 5);
	}

}
