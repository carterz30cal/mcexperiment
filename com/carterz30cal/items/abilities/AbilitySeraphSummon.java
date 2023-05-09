package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.BossWaterwaySeraph;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemReqs;
import com.carterz30cal.utils.EntityUtils;

public class AbilitySeraphSummon extends ItemAbility {

	public AbilitySeraphSummon(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GOLDBOLDChallenge the Seraph";
	}

	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYConsumes this item and grants your party entrance into");
		l.add("GRAYthe DARK_PURPLESeraph'sGRAY lab, where you will surely meet your doom.");
		l.add("DARK_GRAYRight click to use - use at the altar.");
		return l;
	}
	
	public void onRightClick()
	{
		if (!EntityUtils.getNearbyPlayers(BossWaterwaySeraph.altar, 9).contains(owner)) return;
		
		ItemReqs req = new ItemReqs();
		req.addRequirement(new ItemReq("seraph_summon", 1));
		
		if (req.areRequirementsMet(owner) && BossWaterwaySeraph.attemptStartFight())
		{
			req.execute(owner);
		}
	}
}
