package com.carterz30cal.items.abilities.talismans;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

public class TalismanFishCharm extends ItemAbility {

	public TalismanFishCharm(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "BLUEFishy Business";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYIncreases your GREENVitality " + Stat.VITALITY.getIcon() + "GRAY by GREEN10%GRAY but");
		l.add("GRAYreduces your BLUEMight " + Stat.MIGHT.getIcon() + "GRAY by BLUE5%GRAY.");
		return l;
	}
	
	public void onPlayerStats(StatContainer item) {
		item.scheduleOperation(Stat.VITALITY, StatOperationType.CAP_MIN, 10);
		item.scheduleOperation(Stat.VITALITY, StatOperationType.MULTIPLY, 1.1);
		item.scheduleOperation(Stat.MIGHT, StatOperationType.MULTIPLY, 0.95);
	}

}
